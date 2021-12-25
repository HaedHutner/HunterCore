package dev.haedhutner.core.module.config;

import dev.haedhutner.core.module.PluginModule;
import dev.haedhutner.core.utils.PluginConfig;
import org.spongepowered.api.plugin.PluginContainer;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ModuleConfiguration extends PluginConfig {

    protected ModuleConfiguration(PluginContainer plugin, PluginModule module) {
        super(Paths.get(".", "config", plugin.getId(), "modules", module.getId() + ".conf"));
    }
}
