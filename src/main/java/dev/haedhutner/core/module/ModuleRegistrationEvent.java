package dev.haedhutner.core.module;

import com.google.inject.Injector;
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

    public void registerModule(PluginModule module, Injector injector) {
        engine.registerModule(module, injector);
    }

    @Override
    public Cause getCause() {
        return cause;
    }
}
