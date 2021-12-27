package dev.haedhutner.skills.event;

import dev.haedhutner.skills.api.effect.Applyable;
import dev.haedhutner.skills.service.EffectService;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.event.Event;
import org.spongepowered.api.event.cause.Cause;

public class EffectRegistrationEvent implements Event {

    private final Cause cause;

    private final EffectService effectService;

    public EffectRegistrationEvent(EffectService effectService) {
        cause = Cause.of(Sponge.getCauseStackManager().getCurrentContext(), effectService);
        this.effectService = effectService;
    }

    public void registerEffects(Applyable... applyables) {
        effectService.registerEffects(applyables);
    }

    @Override
    public Cause getCause() {
        return null;
    }
}
