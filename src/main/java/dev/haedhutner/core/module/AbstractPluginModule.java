package dev.haedhutner.core.module;

import org.spongepowered.api.plugin.PluginContainer;

public abstract class AbstractPluginModule implements PluginModule {

    private final PluginContainer plugin;

    private final String id;
    private final String name;
    private final String description;

    protected boolean enabled;
    protected boolean started;
    protected boolean shutdown;

    protected AbstractPluginModule(PluginContainer plugin, String id, String name, String description) {
        this.plugin = plugin;
        this.id = id;
        this.name = name;
        this.description = description;
    }

    @Override
    public PluginContainer getPlugin() {
        return plugin;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public boolean isStarted() {
        return started;
    }

    @Override
    public boolean isShutdown() {
        return shutdown;
    }

    @Override
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    protected void setStarted(boolean started) {
        this.started = started;
    }

    protected void setShutdown(boolean shutdown) {
        this.shutdown = shutdown;
    }
}
