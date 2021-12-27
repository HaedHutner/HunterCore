package dev.haedhutner.core.module;

import com.google.inject.Injector;
import dev.haedhutner.core.utils.PluginConfig;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.event.Event;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public final class ModuleEngine {

    private final Logger logger;
    private final Set<PluginModule> modules;

    public ModuleEngine(Logger logger) {
        this.modules = new HashSet<>();
        this.logger = logger;
    }

    public Set<ModuleResult> registerAndInitModules(Map<String, Boolean> configurations) {
        Event event = new ModuleRegistrationEvent(this);
        Sponge.getEventManager().post(event);

        List<String> allModules = modules.stream().map(PluginModule::getName).collect(Collectors.toList());

        logger.info("Registered " + allModules.size() + " modules: " + allModules);

        List<String> disabledModules = configurations.entrySet()
                .stream()
                .filter(e -> !e.getValue()) // filter all disabled
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        if (disabledModules.size() > 0) {
            logger.info("Skipping modules " + disabledModules + " because they are disabled");
        }

        this.modules.forEach(pm -> pm.setEnabled(configurations.getOrDefault(pm.getId(), false)));

        return getEnabledModules().stream()
                .map(pm -> {
                    logger.info("Initializing module " + pm.getName() + "...");

                    pm.getConfiguration().ifPresent(PluginConfig::init);
                    ModuleResult result = ModuleResult.of(pm, pm::init);

                    if (!result.isSuccess()) {
                        logger.error("An error occurred while initializing module " + pm.getName() + ": " + result.getMessage().orElse("Unknown Reason"));
                        result.getException().ifPresent(e -> logger.error(ExceptionUtils.getStackTrace(e)));
                    }

                    return result;
                }).collect(Collectors.toSet());
    }

    public void registerModule(PluginModule module, @Nullable Injector injector) {
        if (injector != null) {
            injector.injectMembers(module);
        }

        this.modules.add(module);
    }

    public Set<ModuleResult> startModules() {
        return getEnabledModules().stream().map(pm -> {
            logger.info("Starting module " + pm.getName() + "...");

            Sponge.getEventManager().registerListeners(pm.getPlugin(), pm);
            ModuleResult result = pm.start();

            if (!result.isSuccess()) {
                logger.error("An error occurred while starting module " + pm.getName() + ": " + result.getMessage().orElse("Unknown Reason"));
                result.getException().ifPresent(e -> logger.error(ExceptionUtils.getStackTrace(e)));
            }

            if (pm instanceof AbstractPluginModule) {
                ((AbstractPluginModule) pm).setStarted(true);
                ((AbstractPluginModule) pm).setShutdown(false);
            }

            return result;
        }).collect(Collectors.toSet());
    }

    public Set<ModuleResult> stopModules() {
        return getStartedModules().stream().map(pm -> {
            logger.info("Stopping module " + pm.getName() + "...");

            Sponge.getEventManager().unregisterListeners(pm);
            ModuleResult result = pm.stop();

            if (!result.isSuccess()) {
                logger.error("An error occurred while stopping module " + pm.getName() + ": " + result.getMessage().orElse("Unknown Reason"));
                result.getException().ifPresent(e -> logger.error(ExceptionUtils.getStackTrace(e)));
            }

            if (pm instanceof AbstractPluginModule) {
                ((AbstractPluginModule) pm).setStarted(false);
                ((AbstractPluginModule) pm).setShutdown(true);
            }

            return result;
        }).collect(Collectors.toSet());
    }

    public Set<ModuleResult> reloadModules() {
        return getStartedModules().stream().map(pm -> {
            logger.info("Reloading module " + pm.getName() + "...");

            ModuleResult result = pm.reload();

            if (!result.isSuccess()) {
                logger.error("An error occurred while reloading module " + pm.getName() + ": " + result.getMessage().orElse("Unknown Reason"));
                result.getException().ifPresent(e -> logger.error(ExceptionUtils.getStackTrace(e)));
            }

            return result;
        }).collect(Collectors.toSet());
    }

    private Set<PluginModule> getEnabledModules() {
        return this.modules.stream().filter(PluginModule::isEnabled).collect(Collectors.toSet());
    }

    private Set<PluginModule> getStartedModules() {
        return this.modules.stream().filter(PluginModule::isStarted).collect(Collectors.toSet());
    }
}
