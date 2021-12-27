package dev.haedhutner.skills;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import dev.haedhutner.core.command.CommandService;
import dev.haedhutner.core.module.AbstractPluginModule;
import dev.haedhutner.core.module.ModuleResult;
import dev.haedhutner.core.module.PluginModule;
import dev.haedhutner.core.module.config.ModuleConfiguration;
import dev.haedhutner.skills.api.skill.Castable;
import dev.haedhutner.skills.command.effect.EffectCommand;
import dev.haedhutner.skills.command.skill.SkillCommand;
import dev.haedhutner.skills.event.EffectRegistrationEvent;
import dev.haedhutner.skills.event.SkillRegistrationEvent;
import dev.haedhutner.skills.facade.EffectFacade;
import dev.haedhutner.skills.facade.SkillFacade;
import dev.haedhutner.skills.facade.SkillMessagingFacade;
import dev.haedhutner.skills.listener.EntityListener;
import dev.haedhutner.skills.listener.ResourceListener;
import dev.haedhutner.skills.service.CooldownService;
import dev.haedhutner.skills.service.EffectService;
import dev.haedhutner.skills.service.ResourceService;
import dev.haedhutner.skills.service.SkillService;
import dev.haedhutner.skills.skill.SimpleDamageEffectSkill;
import dev.haedhutner.skills.skill.SimpleDamageSkill;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.GameRegistryEvent;
import org.spongepowered.api.plugin.PluginContainer;

import java.util.Optional;

@Singleton
public class SkillsModule extends AbstractPluginModule {

    public static final String ID = "skills";

    @Inject
    private Logger logger;

    @Inject
    private SkillsConfig config;

    @Inject
    private EntityListener entityListener;

    @Inject
    private ResourceListener resourceListener;
    @Inject
    private CommandService commandService;

    @Inject
    private SkillCommand skillCommand;

    @Inject
    private EffectCommand effectCommand;

    @Inject
    private Injector injector;

    @Inject
    protected SkillsModule(PluginContainer plugin) {
        super(plugin, ID, "Hunter Skills", "A module for skills");
    }

    @Override
    public ModuleResult init() {
        return ModuleResult.of(this, () -> {
            Sponge.getEventManager().registerListeners(getPlugin(), entityListener);
            Sponge.getEventManager().registerListeners(getPlugin(), resourceListener);

            try {
                commandService.register(skillCommand, getPlugin());
                commandService.register(effectCommand, getPlugin());
            } catch (CommandService.AnnotatedCommandException e) {
                e.printStackTrace();
            }

            return ModuleResult.success(this, "Successfully initialized");
        });
    }

    @Override
    public ModuleResult start() {
        return ModuleResult.of(this, () -> {
            Sponge.getEventManager().post(new EffectRegistrationEvent(getEffectService()));
            Sponge.getEventManager().post(new SkillRegistrationEvent(getSkillService()));

            return ModuleResult.success(this, "Successfully started");
        });
    }

    @Override
    public ModuleResult stop() {
        return ModuleResult.success(this, "Successfully stopped");
    }

    @Override
    public Optional<ModuleConfiguration> getConfiguration() {
        return Optional.of(config);
    }

    @Listener
    public void onSkillRegistration(SkillRegistrationEvent event) {
        logger.info("Registering skills...");
        event.registerSkills(getPlugin(), new SimpleDamageSkill(), new SimpleDamageEffectSkill());
    }

    public SkillsConfig getConfig() {
        return config;
    }

    public EffectService getEffectService() {
        return injector.getInstance(EffectService.class);
    }

    public SkillService getSkillService() {
        return injector.getInstance(SkillService.class);
    }

    public CooldownService getCooldownService() {
        return injector.getInstance(CooldownService.class);
    }

    public ResourceService getResourceService() {
        return injector.getInstance(ResourceService.class);
    }

    public EffectFacade getEffectFacade() {
        return injector.getInstance(EffectFacade.class);
    }

    public SkillFacade getSkillFacade() {
        return injector.getInstance(SkillFacade.class);
    }

    public SkillMessagingFacade getMessagingFacade() {
        return injector.getInstance(SkillMessagingFacade.class);
    }
}
