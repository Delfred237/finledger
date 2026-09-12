package com.portfolio.finledger.exception;

/**
 * Thrown when an operation violates a uniqueness constraint.
 *
 * Example:
 * creating a category with a name that already exists.
 */
public class DuplicateEntityException extends IllegalStateException {

    public DuplicateEntityException(String message) {
        super(message);
    }
}