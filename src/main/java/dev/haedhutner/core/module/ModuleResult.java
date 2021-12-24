package dev.haedhutner.core.module;

public class ModuleResult {

    private Exception exception;

    private boolean success;

    private String message;

    public ModuleResult(boolean success, String message, Exception exception) {
        this.success = success;
        this.message = message;
        this.exception = exception;
    }

    public Exception getException() {
        return exception;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return "ModuleResult{" +
                "exception=" + exception +
                ", success=" + success +
                ", message='" + message + '\'' +
                '}';
    }
}
