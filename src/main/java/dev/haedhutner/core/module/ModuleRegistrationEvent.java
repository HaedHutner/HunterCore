package dev.haedhutner.core.module;

import dev.haedhutner.core.HunterCore;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.event.Event;
import org.spongepowered.api.event.cause.Cause;

public class ModuleRegistrationEvent implements Event {

    private Cause cause;

    private ModuleEngine engine;

    public ModuleRegistrationEvent(ModuleEngine engine) {
        this.cause = Cause.of(Sponge.getCauseStackManager().getCurrentContext(), engine);
        this.engine = engine;
    }

    public void registerModule(Module module) {
        engine.registerModule(module);
    }

    @Override
    public Cause getCause() {
        return cause;
    }
}
