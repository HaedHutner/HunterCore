package dev.haedhutner.core.utils;

import javax.annotation.Nullable;
import java.util.Optional;

/**
 * Intended for methods which need to inform their callers of the state of their execution, whether it is successful
 * or not, and to provide a result in case of a success.
 */
public class OperationResult<T> {

    private final T result;
    private final boolean success;
    private final String message;
    private final Exception exception;

    public OperationResult(@Nullable T result, boolean success, @Nullable String message, @Nullable Exception exception) {
        this.result = result;
        this.success = success;
        this.message = message;
        this.exception = exception;
    }

    public Optional<T> getResult() {
        return Optional.ofNullable(result);
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
