package dev.haedhutner.skills.service;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import dev.haedhutner.skills.SkillsConfig;
import dev.haedhutner.skills.api.event.SkillCastEvent;
import dev.haedhutner.skills.api.exception.CastException;
import dev.haedhutner.skills.api.resource.ResourceUser;
import dev.haedhutner.skills.api.skill.CastErrors;
import dev.haedhutner.skills.api.skill.CastResult;
import dev.haedhutner.skills.api.skill.Castable;
import dev.haedhutner.skills.api.skill.TargetedCastable;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.service.permission.Subject;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Singleton
public class SkillService {

    private Map<String, Castable> skills = new HashMap<>();

    @Inject
    ResourceService resourceService;

    @Inject
    CooldownService cooldownService;

    @Inject
    SkillsConfig config;

    SkillService() {
    }

    public void registerSkill(PluginContainer plugin, Castable castable) {
        skills.put(castable.getId().toLowerCase(), castable);
        Sponge.getEventManager().registerListeners(plugin, castable);
    }

    public void registerSkills(PluginContainer plugin, Castable... castables) {
        for (Castable castable : castables) {
            registerSkill(plugin, castable);
        }
    }

    public Optional<Castable> getSkillById(String id) {
        return Optional.ofNullable(skills.get(id));
    }

    /**
     * Casts a castable with the properties stored within this carrier
     *
     * @param user      The caster casting the skill
     * @param castable  the castable skill
     * @param timestamp When the skill is being cast
     * @param args      arguments
     * @return a {@link CastResult}
     */
    public CastResult castSkill(Living user, Castable castable, long timestamp, String... args) throws CastException {
        // Trigger the pre-cast event
        SkillCastEvent.Pre preCastEvent = new SkillCastEvent.Pre(user, castable, timestamp);
        Sponge.getEventManager().post(preCastEvent);

        // If the pre-cast event was cancelled, throw a cancelled exception
        if (preCastEvent.isCancelled()) {
            throw CastErrors.cancelled(castable);
        }

        // Set the user, skill and skill properties to what was set in the pre-cast event
        user = preCastEvent.getUser();
        castable = preCastEvent.getSkill();

        // Validate
        if (validateSkillUse(user, castable, timestamp)) {

            // If this is a TargetedCastable, set the configured passable blocks.
            // NOTE: It's possible for the implementation of the skill to override and ignore this set function
            if (castable instanceof TargetedCastable) {
                ((TargetedCastable) castable).setPassableBlocks(config.PASSABLE_BLOCKS);
            }

            // Cast the skill
            CastResult result = castable.cast(user, timestamp, args);

            // Set cooldown(s) and withdraw resources
            cooldownService.putOnGlobalCooldown(user, timestamp);
            cooldownService.setLastUsedTimestamp(user, castable, timestamp);
            resourceService.withdrawResource(user, castable.getResourceCost(user));

            // Trigger the post-cast event with the result
            SkillCastEvent.Post postCastEvent = new SkillCastEvent.Post(user, castable, timestamp, result);
            Sponge.getEventManager().post(postCastEvent);

            // Return the result
            return result;
        } else {
            throw CastErrors.internalError();
        }
    }

    private boolean validateSkillUse(Living user, Castable castable, long timestamp) throws CastException {
        boolean valid = validatePermission(user, castable);
        valid = valid && validateGlobalCooldown(user, timestamp);
        valid = valid && validateCooldown(user, castable, timestamp);
        valid = valid && validateResources(user, castable);

        return valid;
    }

    private boolean validatePermission(Living user, Castable skill) throws CastException {
        // If the user is a subject, check for permission.
        // If the user is not a subject, just return true ( is presumed to be non-player character )
        if (user instanceof Subject) {
            String permission = skill.getPermission();

            // If no permission is set, just return true
            if (permission == null) {
                return true;
            }

            boolean permitted = ((Subject) user).hasPermission(permission);

            if (!permitted) {
                throw CastErrors.noPermission(skill);
            }
        }

        return true;
    }

    private boolean validateGlobalCooldown(Living user, Long timestamp) throws CastException {
        if (cooldownService.isOnGlobalCooldown(user, timestamp)) {
            long cooldownEnd = cooldownService.getLastGlobalCooldownEnd(user);
            throw CastErrors.onGlobalCooldown(timestamp, cooldownEnd);
        }

        return true;
    }

    private boolean validateCooldown(Living user, Castable castable, Long timestamp) throws CastException {
        long lastUsed = cooldownService.getLastUsedTimestamp(user, castable);
        long cooldownDuration = castable.getCooldown(user);

        if (cooldownService.isCooldownOngoing(timestamp, lastUsed, cooldownDuration)) {
            long cooldownEnd = cooldownService.getCooldownEnd(lastUsed, cooldownDuration);
            throw CastErrors.onCooldown(timestamp, castable, cooldownEnd);
        }

        return true;
    }

    private boolean validateResources(Living user, Castable castable) throws CastException {
        ResourceUser resourceUser = resourceService.getOrCreateUser(user);

        if (resourceUser.getCurrent() < castable.getResourceCost(user)) {
            throw CastErrors.insufficientResources(castable, config.RESOURCE_NAME);
        }

        return true;
    }

    public Map<String, Castable> getAllSkills() {
        return skills;
    }
}
