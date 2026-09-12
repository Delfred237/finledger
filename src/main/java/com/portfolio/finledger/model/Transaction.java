package com.portfolio.finledger.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.UUID;
import java.util.Objects;

/**
 * Represents a financial transaction.
 *
 * A transaction is either an income or an expense.
 * The stored amount is always positive.
 * The effective sign for balance computation is derived from the type.
 */
public final class Transaction implements Identifiable {

    public static final int DESCRIPTION_MAX_LENGTH = 200;

    private static final int MONEY_SCALE = 2;

    private final UUID id;

    private TransactionType type;
    private BigDecimal amount;
    private Category category;
    private LocalDate date;
    private String description;

    public Transaction(TransactionType type, BigDecimal amount, Category category, LocalDate date) {
        this(type, amount, category, date, null);
    }

    public Transaction(
            TransactionType type,
            BigDecimal amount,
            Category category,
            LocalDate date,
            String description
    ) {
        this.id = UUID.randomUUID();

        // Setters are used to apply validation rules consistently.
        setType(type);
        setAmount(amount);
        setCategory(category);
        setDate(date);
        setDescription(description);
    }

    @Override
    public UUID getId() {
        return id;
    }

    public TransactionType getType() {
        return type;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public Category getCategory() {
        return category;
    }

    public LocalDate getDate() {
        return date;
    }

    public String getDescription() {
        return description;
    }

    /**
     * Returns the amount with the sign corresponding to the transaction type.
     *
     * Income returns a positive amount.
     * Expense returns a negative amount.
     *
     * @return signed amount
     */
    public BigDecimal getSignedAmount() {
        return type.applyToBalance(amount);
    }

    public void setType(TransactionType type) {
        this.type = validateType(type);
    }

    public void setAmount(BigDecimal amount) {
        this.amount = validateAmount(amount);
    }

    public void setCategory(Category category) {
        this.category = validateCategory(category);
    }

    public void setDate(LocalDate date) {
        this.date = validateDate(date);
    }

    public void setDescription(String description) {
        this.description = normalizeDescription(description);
    }

    private static TransactionType validateType(TransactionType type) {
        if (type == null) {
            throw new IllegalArgumentException("Transaction type must not be null.");
        }

        return type;
    }

    private static BigDecimal validateAmount(BigDecimal amount) {
        if (amount == null) {
            throw new IllegalArgumentException("Transaction amount must not be null.");
        }

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Transaction amount must be positive.");
        }

        try {
            // Reject amounts requiring rounding beyond 2 decimal places.
            return amount.setScale(MONEY_SCALE, RoundingMode.UNNECESSARY);
        } catch (ArithmeticException exception) {
            throw new IllegalArgumentException(
                    "Transaction amount must have at most " + MONEY_SCALE + " decimal places.",
                    exception
            );
        }
    }

    private static Category validateCategory(Category category) {
        if (category == null) {
            throw new IllegalArgumentException("Transaction category must not be null.");
        }

        return category;
    }

    private static LocalDate validateDate(LocalDate date) {
        if (date == null) {
            throw new IllegalArgumentException("Transaction date must not be null.");
        }

        return date;
    }

    private static String normalizeDescription(String description) {
        if (description == null) {
            return "";
        }

        String trimmed = description.trim();

        if (trimmed.length() > DESCRIPTION_MAX_LENGTH) {
            throw new IllegalArgumentException(
                    "Transaction description must not exceed " + DESCRIPTION_MAX_LENGTH + " characters."
            );
        }

        return trimmed;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }

        if (!(other instanceof Transaction transaction)) {
            return false;
        }

        return Objects.equals(id, transaction.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Transaction{id=%s, type=%s, amount=%s, signedAmount=%s, category='%s', date=%s, description='%s'}"
                .formatted(
                        id,
                        type,
                        amount,
                        getSignedAmount(),
                        category.getName(),
                        date,
                        description
                );
    }
}