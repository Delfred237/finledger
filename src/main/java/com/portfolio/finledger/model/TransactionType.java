package com.portfolio.finledger.model;

import java.math.BigDecimal;

/**
 * Type of a financial transaction.
 *
 * The amount stored on a transaction is always positive.
 * The sign applied to the balance is determined by the transaction type.
 */
public enum TransactionType {

    INCOME("Income") {
        @Override
        public BigDecimal applyToBalance(BigDecimal amount) {
            return validateAmountForBalance(amount);
        }
    },

    EXPENSE("Expense") {
        @Override
        public BigDecimal applyToBalance(BigDecimal amount) {
            return validateAmountForBalance(amount).negate();
        }
    };

    private final String displayName;

    TransactionType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public boolean isIncome() {
        return this == INCOME;
    }

    public boolean isExpense() {
        return this == EXPENSE;
    }

    /**
     * Returns the signed amount to use when computing the balance.
     *
     * Income keeps the amount positive.
     * Expense returns the amount as negative.
     *
     * @param amount positive transaction amount
     * @return signed amount for balance computation
     */
    public abstract BigDecimal applyToBalance(BigDecimal amount);

    private static BigDecimal validateAmountForBalance(BigDecimal amount) {
        if (amount == null) {
            throw new IllegalArgumentException("Amount must not be null.");
        }

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be positive.");
        }

        return amount;
    }
}