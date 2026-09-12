package com.portfolio.finledger.exception;

/**
 * Thrown when persistence fails.
 *
 * This exception wraps low-level I/O problems such as IOException,
 * so the rest of the application does not need to deal with them directly.
 */
public class StorageException extends RuntimeException {

    public StorageException(String message) {
        super(message);
    }

    public StorageException(String message, Throwable cause) {
        super(message, cause);
    }
}