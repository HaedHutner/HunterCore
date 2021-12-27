package dev.haedhutner.core.utils;

import javax.annotation.Nullable;
import java.util.Optional;

/**
 * Intended for methods which need to inform their callers of the state of their execution, whether it is successful
 * or not, and to provide a result in case of a success.
 */
public class OperationResult<T> extends SimpleOperationResult {

    private final T result;

    public OperationResult(@Nullable T result, boolean success, @Nullable String message, @Nullable Exception exception) {
        super(success, message, exception);
        this.result = result;
    }

    public Optional<T> getResult() {
        return Optional.ofNullable(result);
    }
}
