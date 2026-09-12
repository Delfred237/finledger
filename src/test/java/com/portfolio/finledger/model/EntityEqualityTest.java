package com.portfolio.finledger.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class EntityEqualityTest {

    @Test
    void categoryEqualityShouldBeBasedOnId() {
        Category first = new Category("Groceries");
        Category second = new Category("Groceries");

        assertEquals(first, first);
        assertNotEquals(first, second);
        assertNotEquals(first, null);
    }

    @Test
    void transactionEqualityShouldBeBasedOnId() {
        Category category = new Category("Groceries");
        LocalDate date = LocalDate.of(2026, 9, 12);

        Transaction first = new Transaction(
                TransactionType.EXPENSE,
                new BigDecimal("10.00"),
                category,
                date
        );

        Transaction second = new Transaction(
                TransactionType.EXPENSE,
                new BigDecimal("10.00"),
                category,
                date
        );

        assertEquals(first, first);
        assertNotEquals(first, second);
        assertNotEquals(first, null);
    }
}