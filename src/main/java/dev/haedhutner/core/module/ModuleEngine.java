package dev.haedhutner.core.module;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.event.Event;

import java.util.HashSet;
import java.util.Set;

public final class ModuleEngine {

    private Set<Module> modules;

    public ModuleEngine() {
        modules = new HashSet<>();
    }

    public void registerModules() {
        Event event = new ModuleRegistrationEvent(this);
        Sponge.getEventManager().post(event);
    }

    public void registerModule(Module module) {
        this.modules.add(module);
    }
}
