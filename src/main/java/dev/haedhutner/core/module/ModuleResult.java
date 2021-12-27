package dev.haedhutner.core.module;

import dev.haedhutner.core.utils.SimpleOperationResult;

import java.util.function.Supplier;

public class ModuleResult extends SimpleOperationResult {

    private final PluginModule module;

    public static ModuleResult of(PluginModule module, Supplier<ModuleResult> supplier) {
        try {
            return supplier.get();
        } catch (Exception e) {
            return ModuleResult.failure(module, e.getMessage(), e);
        }
    }

    public static ModuleResult failure(PluginModule module, String message, Exception exception) {
        return new ModuleResult(module, false, message, exception);
    }

    public static ModuleResult success(PluginModule module, String message) {
        return new ModuleResult(module, true, message, null);
    }

    public static ModuleResult success(PluginModule module) {
        return new ModuleResult(module, true, "Success", null);
    }

    public ModuleResult(PluginModule module, boolean success, String message, Exception exception) {
        super(success, message, exception);
        this.module = module;
    }

    public PluginModule getModule() {
        return module;
    }
}
