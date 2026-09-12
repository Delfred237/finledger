package com.portfolio.finledger.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

/**
 * Immutable summary used for the dashboard.
 *
 * Amounts are normalized to 2 decimal places.
 */
public record DashboardSummary(
        BigDecimal balance,
        BigDecimal totalIncome,
        BigDecimal totalExpense,
        int transactionCount
) {

    private static final int MONEY_SCALE = 2;

    public DashboardSummary {
        Objects.requireNonNull(balance, "balance must not be null");
        Objects.requireNonNull(totalIncome, "totalIncome must not be null");
        Objects.requireNonNull(totalExpense, "totalExpense must not be null");

        if (transactionCount < 0) {
            throw new IllegalArgumentException("transactionCount must not be negative");
        }

        balance = normalize(balance);
        totalIncome = normalize(totalIncome);
        totalExpense = normalize(totalExpense);
    }

    private static BigDecimal normalize(BigDecimal value) {
        return value.setScale(MONEY_SCALE, RoundingMode.UNNECESSARY);
    }
}