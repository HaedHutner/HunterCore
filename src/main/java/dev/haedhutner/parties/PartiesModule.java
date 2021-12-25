package dev.haedhutner.parties;

import dev.haedhutner.core.module.AbstractPluginModule;
import dev.haedhutner.core.module.ModuleResult;
import dev.haedhutner.core.module.config.ModuleConfiguration;
import org.spongepowered.api.plugin.PluginContainer;

public class PartiesModule extends AbstractPluginModule {
    protected PartiesModule(PluginContainer container) {
        super(
                container,
                "parties",
                "Hunter Parties",
                "A module to add simple party functionality"
        );
    }

    @Override
    public ModuleResult init() {
        return null;
    }

    @Override
    public ModuleResult start() {
        return null;
    }

    @Override
    public ModuleResult stop() {
        return null;
    }

    @Override
    public ModuleConfiguration getConfiguration() {
        return null;
    }
}
