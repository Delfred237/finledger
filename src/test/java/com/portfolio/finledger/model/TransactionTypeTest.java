package com.portfolio.finledger.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TransactionTypeTest {

    @Test
    void incomeApplyToBalanceShouldReturnPositiveAmount() {
        BigDecimal amount = new BigDecimal("100.00");

        BigDecimal result = TransactionType.INCOME.applyToBalance(amount);

        assertEquals(0, amount.compareTo(result));
    }

    @Test
    void expenseApplyToBalanceShouldReturnNegativeAmount() {
        BigDecimal amount = new BigDecimal("30.50");
        BigDecimal expected = new BigDecimal("-30.50");

        BigDecimal result = TransactionType.EXPENSE.applyToBalance(amount);

        assertEquals(0, expected.compareTo(result));
    }

    @Test
    void incomeShouldBeIncomeAndNotExpense() {
        assertTrue(TransactionType.INCOME.isIncome());
        assertFalse(TransactionType.INCOME.isExpense());
    }

    @Test
    void expenseShouldBeExpenseAndNotIncome() {
        assertTrue(TransactionType.EXPENSE.isExpense());
        assertFalse(TransactionType.EXPENSE.isIncome());
    }
}