package com.portfolio.finledger.exception;

import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ValidationExceptionTest {

    @Test
    void messageOnlyShouldHaveEmptyField() {
        ValidationException exception = new ValidationException("Amount must be positive.");

        assertEquals("Amount must be positive.", exception.getMessage());
        assertTrue(exception.getField().isEmpty());
    }

    @Test
    void fieldAndMessageShouldExposeField() {
        ValidationException exception = new ValidationException(
                "amount",
                "Amount must be positive."
        );

        Optional<String> field = exception.getField();

        assertTrue(field.isPresent());
        assertEquals("amount", field.get());
    }
}