package com.portfolio.finledger.service;

import com.portfolio.finledger.model.Category;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

/**
 * Immutable statistic for a category's expenses.
 */
public record CategoryExpense(
        Category category,
        BigDecimal totalExpense,
        long transactionCount
) {

    private static final int MONEY_SCALE = 2;

    public CategoryExpense {
        Objects.requireNonNull(category, "category must not be null");
        Objects.requireNonNull(totalExpense, "totalExpense must not be null");

        if (transactionCount < 0) {
            throw new IllegalArgumentException("transactionCount must not be negative");
        }

        totalExpense = normalize(totalExpense);
    }

    private static BigDecimal normalize(BigDecimal value) {
        return value.setScale(MONEY_SCALE, RoundingMode.UNNECESSARY);
    }
}