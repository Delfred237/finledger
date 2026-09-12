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
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TransactionStatisticsServiceTest {

    private InMemoryTransactionRepository repository;
    private TransactionStatisticsService service;

    private Category groceries;
    private Category transport;
    private Category salary;

    @BeforeEach
    void setUp() {
        repository = new InMemoryTransactionRepository();
        service = new TransactionStatisticsService(repository);

        groceries = new Category("Groceries");
        transport = new Category("Transport");
        salary = new Category("Salary");
    }

    private void addExpense(Category category, String amount, LocalDate date) {
        repository.add(
                new Transaction(
                        TransactionType.EXPENSE,
                        new BigDecimal(amount),
                        category,
                        date
                )
        );
    }

    private void addIncome(Category category, String amount, LocalDate date) {
        repository.add(
                new Transaction(
                        TransactionType.INCOME,
                        new BigDecimal(amount),
                        category,
                        date
                )
        );
    }

    private static void assertMoneyEquals(String expected, BigDecimal actual) {
        assertNotNull(actual);
        assertEquals(0, new BigDecimal(expected).compareTo(actual));
    }

    @Test
    void expensesByCategoryShouldGroupAndSortExpenses() {
        addExpense(groceries, "30.00", LocalDate.of(2026, 9, 1));
        addExpense(groceries, "20.00", LocalDate.of(2026, 9, 5));
        addExpense(transport, "60.00", LocalDate.of(2026, 9, 10));
        addIncome(salary, "1000.00", LocalDate.of(2026, 9, 1));

        List<CategoryExpense> result = service.getExpensesByCategory();

        assertEquals(2, result.size());

        assertEquals(transport, result.get(0).category());
        assertMoneyEquals("60.00", result.get(0).totalExpense());
        assertEquals(1L, result.get(0).transactionCount());

        assertEquals(groceries, result.get(1).category());
        assertMoneyEquals("50.00", result.get(1).totalExpense());
        assertEquals(2L, result.get(1).transactionCount());
    }

    @Test
    void findTopExpenseCategoryShouldReturnHighestExpenseCategory() {
        addExpense(groceries, "30.00", LocalDate.of(2026, 9, 1));
        addExpense(transport, "60.00", LocalDate.of(2026, 9, 10));

        CategoryExpense top = service.findTopExpenseCategory().orElseThrow();

        assertEquals(transport, top.category());
        assertMoneyEquals("60.00", top.totalExpense());
    }

    @Test
    void findTopExpenseCategoryShouldBeEmptyWhenNoExpenseExists() {
        addIncome(salary, "1000.00", LocalDate.of(2026, 9, 1));

        assertTrue(service.findTopExpenseCategory().isEmpty());
    }

    @Test
    void monthlyEvolutionShouldGroupTransactionsByMonth() {
        addIncome(salary, "1200.00", LocalDate.of(2026, 9, 1));
        addExpense(groceries, "300.00", LocalDate.of(2026, 9, 15));
        addExpense(transport, "100.00", LocalDate.of(2026, 10, 2));

        List<MonthlySummary> evolution = service.getMonthlyEvolution(
                LocalDate.of(2026, 9, 1),
                LocalDate.of(2026, 10, 31)
        );

        assertEquals(2, evolution.size());

        MonthlySummary september = evolution.get(0);
        assertEquals(YearMonth.of(2026, 9), september.month());
        assertMoneyEquals("1200.00", september.income());
        assertMoneyEquals("300.00", september.expense());
        assertMoneyEquals("900.00", september.net());
        assertEquals(2, september.transactionCount());

        MonthlySummary october = evolution.get(1);
        assertEquals(YearMonth.of(2026, 10), october.month());
        assertMoneyEquals("0.00", october.income());
        assertMoneyEquals("100.00", october.expense());
        assertMoneyEquals("-100.00", october.net());
        assertEquals(1, october.transactionCount());
    }

    @Test
    void monthlyEvolutionShouldBeEmptyWhenNoTransactionInRange() {
        List<MonthlySummary> evolution = service.getMonthlyEvolution(
                LocalDate.of(2027, 1, 1),
                LocalDate.of(2027, 1, 31)
        );

        assertTrue(evolution.isEmpty());
    }
}