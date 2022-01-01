package dev.haedhutner.core.utils;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.scheduler.Task;

@Singleton
public final class AsyncExecutionService {

    private final PluginContainer plugin;

    @Inject
    public AsyncExecutionService(PluginContainer plugin) {
        this.plugin = plugin;
    }

    public PluginContainer getPlugin() {
        return plugin;
    }

    public void executeMain(Runnable runnable) {
        Task.builder()
                .delayTicks(0)
                .execute(runnable)
                .submit(plugin);
    }

}
