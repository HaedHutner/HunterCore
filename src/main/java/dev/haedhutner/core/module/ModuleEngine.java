package dev.haedhutner.core.module;

import com.google.inject.Injector;
import dev.haedhutner.core.module.config.ModulesConfiguration;
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

    public Set<ModuleResult> registerAndInitModules(ModulesConfiguration configurations) {
        Event event = new ModuleRegistrationEvent(this);
        Sponge.getEventManager().post(event);

        List<String> allModules = modules.stream().map(PluginModule::getName).collect(Collectors.toList());

        logger.info("Registered " + allModules.size() + " modules: " + allModules);

        List<String> disabledModules = configurations.entrySet()
                .stream()
                .filter(e -> !e.getValue()) // filter all disabled
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        logger.info("Skipping modules " + disabledModules + " because they are disabled");

        return this.modules.stream()
                .filter(pm -> !disabledModules.contains(pm.getId()))
                .map(pm -> {
                    logger.info("Initializing module " + pm.getName() + "...");

                    ModuleResult result = ModuleResult.of(pm, () -> {
                        pm.getConfiguration().init();
                        return pm.init();
                    });

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
        return this.modules.stream().map(pm -> {
            logger.info("Starting module " + pm.getName() + "...");

            Sponge.getEventManager().registerListeners(pm.getPlugin(), pm);
            ModuleResult result = pm.start();

            if (!result.isSuccess()) {
                logger.error("An error occurred while starting module " + pm.getName() + ": " + result.getMessage().orElse("Unknown Reason"));
                result.getException().ifPresent(e -> logger.error(ExceptionUtils.getStackTrace(e)));
            }

            return result;
        }).collect(Collectors.toSet());
    }

    public Set<ModuleResult> stopModules() {
        return this.modules.stream().map(pm -> {
            logger.info("Stopping module " + pm.getName() + "...");

            Sponge.getEventManager().unregisterListeners(pm);
            ModuleResult result = pm.stop();

            if (!result.isSuccess()) {
                logger.error("An error occurred while stopping module " + pm.getName() + ": " + result.getMessage().orElse("Unknown Reason"));
                result.getException().ifPresent(e -> logger.error(ExceptionUtils.getStackTrace(e)));
            }

            return result;
        }).collect(Collectors.toSet());
    }
}
