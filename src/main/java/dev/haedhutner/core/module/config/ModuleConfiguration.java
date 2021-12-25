package dev.haedhutner.core.module.config;

import dev.haedhutner.core.module.PluginModule;
import dev.haedhutner.core.utils.PluginConfig;
import dev.haedhutner.core.utils.SimpleOperationResult;
import org.spongepowered.api.plugin.PluginContainer;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ModuleConfiguration extends PluginConfig {

    public ModuleConfiguration() {
        super(null);
    }

    protected ModuleConfiguration(PluginContainer plugin, PluginModule module) {
        super(determineFilePath(plugin, module));
    }

    public SimpleOperationResult init(PluginContainer plugin, PluginModule module) {
        super.filePath = determineFilePath(plugin, module);
        return super.init();
    }

    private static Path determineFilePath(PluginContainer plugin, PluginModule module) {
        return Paths.get(".", "config", plugin.getId(), "modules", module.getId() + ".conf");
    }
}
