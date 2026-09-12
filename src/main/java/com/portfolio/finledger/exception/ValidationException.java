package com.portfolio.finledger.exception;

import java.util.Optional;

/**
 * Thrown when input or domain data violates a validation rule.
 *
 * Extends IllegalArgumentException so existing validation tests and catch blocks
 * remain compatible, while allowing more precise handling when needed.
 */
public class ValidationException extends IllegalArgumentException {

    private final String field;

    public ValidationException(String message) {
        this(null, message);
    }

    public ValidationException(String field, String message) {
        super(message);
        this.field = field;
    }

    public ValidationException(String message, Throwable cause) {
        this(null, message, cause);
    }

    public ValidationException(String field, String message, Throwable cause) {
        super(message, cause);
        this.field = field;
    }

    /**
     * Returns the field concerned by the validation error, if known.
     *
     * @return optional field name
     */
    public Optional<String> getField() {
        return Optional.ofNullable(field);
    }
}