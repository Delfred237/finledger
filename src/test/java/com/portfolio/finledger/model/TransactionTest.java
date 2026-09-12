package com.portfolio.finledger.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TransactionTest {

    private Category createCategory() {
        return new Category("Groceries");
    }

    private LocalDate createDate() {
        return LocalDate.of(2026, 9, 12);
    }

    @Test
    void createValidTransactionShouldStoreData() {
        Category category = createCategory();
        LocalDate date = createDate();

        Transaction transaction = new Transaction(
                TransactionType.EXPENSE,
                new BigDecimal("25.50"),
                category,
                date,
                "  Supermarket  "
        );

        assertEquals(TransactionType.EXPENSE, transaction.getType());
        assertEquals(0, new BigDecimal("25.50").compareTo(transaction.getAmount()));
        assertSame(category, transaction.getCategory());
        assertEquals(date, transaction.getDate());
        assertEquals("Supermarket", transaction.getDescription());
        assertNotNull(transaction.getId());
    }

    @Test
    void createTransactionShouldNormalizeAmountToTwoDecimalPlaces() {
        Transaction transaction = new Transaction(
                TransactionType.EXPENSE,
                new BigDecimal("25.5"),
                createCategory(),
                createDate()
        );

        assertEquals(0, new BigDecimal("25.50").compareTo(transaction.getAmount()));
    }

    @Test
    void createTransactionWithNullTypeShouldThrow() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new Transaction(
                        null,
                        new BigDecimal("10.00"),
                        createCategory(),
                        createDate()
                )
        );
    }

    @Test
    void createTransactionWithNullAmountShouldThrow() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new Transaction(
                        TransactionType.EXPENSE,
                        null,
                        createCategory(),
                        createDate()
                )
        );
    }

    @Test
    void createTransactionWithZeroAmountShouldThrow() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new Transaction(
                        TransactionType.EXPENSE,
                        new BigDecimal("0.00"),
                        createCategory(),
                        createDate()
                )
        );
    }

    @Test
    void createTransactionWithNegativeAmountShouldThrow() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new Transaction(
                        TransactionType.EXPENSE,
                        new BigDecimal("-10.00"),
                        createCategory(),
                        createDate()
                )
        );
    }

    @Test
    void createTransactionWithTooManyDecimalPlacesShouldThrow() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new Transaction(
                        TransactionType.EXPENSE,
                        new BigDecimal("10.555"),
                        createCategory(),
                        createDate()
                )
        );
    }

    @Test
    void createTransactionWithNullCategoryShouldThrow() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new Transaction(
                        TransactionType.EXPENSE,
                        new BigDecimal("10.00"),
                        null,
                        createDate()
                )
        );
    }

    @Test
    void createTransactionWithNullDateShouldThrow() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new Transaction(
                        TransactionType.EXPENSE,
                        new BigDecimal("10.00"),
                        createCategory(),
                        null
                )
        );
    }

    @Test
    void createTransactionWithNullDescriptionShouldStoreEmptyDescription() {
        Transaction transaction = new Transaction(
                TransactionType.EXPENSE,
                new BigDecimal("10.00"),
                createCategory(),
                createDate(),
                null
        );

        assertEquals("", transaction.getDescription());
    }

    @Test
    void getSignedAmountShouldBePositiveForIncome() {
        Transaction transaction = new Transaction(
                TransactionType.INCOME,
                new BigDecimal("100.00"),
                createCategory(),
                createDate()
        );

        assertEquals(0, new BigDecimal("100.00").compareTo(transaction.getSignedAmount()));
    }

    @Test
    void getSignedAmountShouldBeNegativeForExpense() {
        Transaction transaction = new Transaction(
                TransactionType.EXPENSE,
                new BigDecimal("25.50"),
                createCategory(),
                createDate()
        );

        assertEquals(0, new BigDecimal("-25.50").compareTo(transaction.getSignedAmount()));
    }
}