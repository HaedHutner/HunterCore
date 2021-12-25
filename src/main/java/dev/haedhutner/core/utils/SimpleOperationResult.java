package dev.haedhutner.core.utils;

import javax.annotation.Nullable;
import java.util.Optional;

/**
 * Intended for methods which need to inform their callers of the state of their execution ( either success or not, in this case )
 */
public class SimpleOperationResult {

    private final boolean success;
    private final String message;
    private final Exception exception;

    public SimpleOperationResult(boolean success, @Nullable String message, @Nullable Exception e) {
        this.success = success;
        this.message = message;
        this.exception = e;
    }

    public boolean isSuccess() {
        return success;
    }

    public Optional<String> getMessage() {
        return Optional.ofNullable(message);
    }

    public Optional<Exception> getException() {
        return Optional.ofNullable(exception);
    }

}
