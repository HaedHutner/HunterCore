package dev.haedhutner.core.module.config;

import dev.haedhutner.core.module.PluginModule;
import dev.haedhutner.core.utils.PluginConfig;
import org.spongepowered.api.plugin.PluginContainer;

import java.nio.file.Path;
import java.nio.file.Paths;

public class ModuleConfiguration extends PluginConfig {

    public ModuleConfiguration() {
        super(null);
    }

    protected ModuleConfiguration(PluginContainer plugin, PluginModule module) {
        super(determineFilePath(plugin, module.getId()));
    }

    protected ModuleConfiguration(PluginContainer plugin, String moduleId) {
        super(determineFilePath(plugin, moduleId));
    }

    private static Path determineFilePath(PluginContainer plugin, String moduleId) {
        return Paths.get(".", "config", plugin.getId(), "modules", moduleId + ".conf");
    }
}
