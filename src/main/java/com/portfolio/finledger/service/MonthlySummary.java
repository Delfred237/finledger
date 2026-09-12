package com.portfolio.finledger.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.YearMonth;
import java.util.Objects;

/**
 * Immutable summary for a given month.
 */
public record MonthlySummary(
        YearMonth month,
        BigDecimal income,
        BigDecimal expense,
        BigDecimal net,
        int transactionCount
) {

    private static final int MONEY_SCALE = 2;

    public MonthlySummary {
        Objects.requireNonNull(month, "month must not be null");
        Objects.requireNonNull(income, "income must not be null");
        Objects.requireNonNull(expense, "expense must not be null");
        Objects.requireNonNull(net, "net must not be null");

        if (transactionCount < 0) {
            throw new IllegalArgumentException("transactionCount must not be negative");
        }

        income = normalize(income);
        expense = normalize(expense);
        net = normalize(net);
    }

    private static BigDecimal normalize(BigDecimal value) {
        return value.setScale(MONEY_SCALE, RoundingMode.UNNECESSARY);
    }
}