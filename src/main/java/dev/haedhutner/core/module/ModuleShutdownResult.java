package dev.haedhutner.core.module;

public final class ModuleShutdownResult extends ModuleResult {

    public static ModuleResult failure(String message, Exception exception) {
        return new ModuleShutdownResult(false, message, exception);
    }

    public static ModuleResult success(String message) {
        return new ModuleShutdownResult(true, message, null);
    }


    public ModuleShutdownResult(boolean success, String message, Exception exception) {
        super(success, message, exception);
    }
}
