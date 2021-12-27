package dev.haedhutner.core.module;

import dev.haedhutner.core.module.config.ModuleConfiguration;
import org.spongepowered.api.plugin.PluginContainer;

import java.io.IOException;
import java.util.Optional;

public interface PluginModule {

    PluginContainer getPlugin();

    String getId();

    String getName();

    String getDescription();

    boolean isEnabled();

    void setEnabled(boolean enabled);

    boolean isStarted();

    boolean isShutdown();

    ModuleResult init();

    ModuleResult start();

    ModuleResult stop();

    Optional<ModuleConfiguration> getConfiguration();
}
