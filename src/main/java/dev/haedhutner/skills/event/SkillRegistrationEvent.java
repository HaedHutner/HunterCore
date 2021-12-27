package dev.haedhutner.skills.event;

import dev.haedhutner.skills.api.skill.Castable;
import dev.haedhutner.skills.service.SkillService;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.event.Event;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.plugin.PluginContainer;

public class SkillRegistrationEvent implements Event {

    private final Cause cause;

    private final SkillService skillService;

    public SkillRegistrationEvent(SkillService skillService) {
        cause = Cause.of(Sponge.getCauseStackManager().getCurrentContext(), skillService);
        this.skillService = skillService;
    }

    public void registerSkills(PluginContainer plugin, Castable... castables) {
        skillService.registerSkills(plugin, castables);
    }

    @Override
    public Cause getCause() {
        return cause;
    }
}
