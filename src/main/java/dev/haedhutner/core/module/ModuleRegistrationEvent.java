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

    public void registerModules(Injector injector, PluginModule... modules) {
        for (PluginModule module : modules) {
            registerModule(module, injector);
        }
    }

    public void registerModules(PluginModule... modules) {
        for (PluginModule module : modules) {
            registerModule(module);
        }
    }

    public void registerModule(PluginModule module) {
        engine.registerModule(module, null);
    }

    public void registerModule(PluginModule module, Injector injector) {
        engine.registerModule(module, injector);
    }

    @Override
    public Cause getCause() {
        return cause;
    }
}
