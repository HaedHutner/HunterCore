package dev.haedhutner.core.module;

import dev.haedhutner.core.module.config.ModuleConfiguration;
import org.spongepowered.api.plugin.PluginContainer;

import java.io.IOException;

public interface PluginModule {

    PluginContainer getPlugin();

    String getId();

    String getName();

    String getDescription();

    boolean isEnabled();

    boolean isStarted();

    boolean isShutdown();

    ModuleResult init();

    ModuleResult start();

    ModuleResult stop();

    ModuleConfiguration getConfiguration();
}
