package com.portfolio.finledger.service;

import com.portfolio.finledger.model.Category;
import com.portfolio.finledger.model.Transaction;
import com.portfolio.finledger.model.TransactionType;
import com.portfolio.finledger.repository.memory.InMemoryTransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class TransactionSummaryServiceTest {

    private InMemoryTransactionRepository repository;
    private TransactionSummaryService service;

    private Category salary;
    private Category groceries;

    @BeforeEach
    void setUp() {
        repository = new InMemoryTransactionRepository();
        service = new TransactionSummaryService(repository);

        salary = new Category("Salary");
        groceries = new Category("Groceries");
    }

    private void addIncome(BigDecimal amount, LocalDate date) {
        repository.add(
                new Transaction(
                        TransactionType.INCOME,
                        amount,
                        salary,
                        date
                )
        );
    }

    private void addExpense(BigDecimal amount, LocalDate date) {
        repository.add(
                new Transaction(
                        TransactionType.EXPENSE,
                        amount,
                        groceries,
                        date
                )
        );
    }

    private static void assertMoneyEquals(String expected, BigDecimal actual) {
        assertNotNull(actual);
        assertEquals(0, new BigDecimal(expected).compareTo(actual));
    }

    @Test
    void emptyDashboardShouldReturnZeros() {
        DashboardSummary dashboard = service.getDashboard();

        assertMoneyEquals("0.00", dashboard.balance());
        assertMoneyEquals("0.00", dashboard.totalIncome());
        assertMoneyEquals("0.00", dashboard.totalExpense());
        assertEquals(0, dashboard.transactionCount());
    }

    @Test
    void dashboardShouldCalculateTotalsAndBalance() {
        addIncome(new BigDecimal("1200.00"), LocalDate.of(2026, 9, 1));
        addExpense(new BigDecimal("250.50"), LocalDate.of(2026, 9, 10));
        addExpense(new BigDecimal("49.50"), LocalDate.of(2026, 9, 12));

        DashboardSummary dashboard = service.getDashboard();

        assertMoneyEquals("1200.00", dashboard.totalIncome());
        assertMoneyEquals("300.00", dashboard.totalExpense());
        assertMoneyEquals("900.00", dashboard.balance());
        assertEquals(3, dashboard.transactionCount());
    }

    @Test
    void monthlySummaryShouldIncludeOnlyTransactionsInRequestedMonth() {
        addIncome(new BigDecimal("1200.00"), LocalDate.of(2026, 9, 1));
        addExpense(new BigDecimal("250.00"), LocalDate.of(2026, 9, 15));
        addExpense(new BigDecimal("100.00"), LocalDate.of(2026, 10, 1));

        MonthlySummary summary = service.getMonthlySummary(YearMonth.of(2026, 9));

        assertEquals(YearMonth.of(2026, 9), summary.month());
        assertMoneyEquals("1200.00", summary.income());
        assertMoneyEquals("250.00", summary.expense());
        assertMoneyEquals("950.00", summary.net());
        assertEquals(2, summary.transactionCount());
    }

    @Test
    void monthlySummaryShouldReturnZerosForEmptyMonth() {
        MonthlySummary summary = service.getMonthlySummary(YearMonth.of(2026, 11));

        assertEquals(YearMonth.of(2026, 11), summary.month());
        assertMoneyEquals("0.00", summary.income());
        assertMoneyEquals("0.00", summary.expense());
        assertMoneyEquals("0.00", summary.net());
        assertEquals(0, summary.transactionCount());
    }
}