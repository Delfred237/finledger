package com.portfolio.finledger.repository.memory;

import com.portfolio.finledger.model.Category;
import com.portfolio.finledger.model.Transaction;
import com.portfolio.finledger.model.TransactionType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class InMemoryTransactionRepositoryTest {

    private InMemoryTransactionRepository repository;

    private Category groceries;
    private Category salary;

    private LocalDate september;

    @BeforeEach
    void setUp() {
        repository = new InMemoryTransactionRepository();

        groceries = new Category("Groceries");
        salary = new Category("Salary");

        september = LocalDate.of(2026, 9, 1);
    }

    @Test
    void addShouldStoreTransaction() {
        Transaction transaction = new Transaction(
                TransactionType.EXPENSE,
                new BigDecimal("25.50"),
                groceries,
                september
        );

        repository.add(transaction);

        assertEquals(1, repository.count());
    }

    @Test
    void findByTypeShouldFilterTransactions() {
        repository.add(
                new Transaction(
                        TransactionType.EXPENSE,
                        new BigDecimal("25.50"),
                        groceries,
                        september
                )
        );

        repository.add(
                new Transaction(
                        TransactionType.INCOME,
                        new BigDecimal("1200.00"),
                        salary,
                        september
                )
        );

        List<Transaction> incomes = repository.findByType(TransactionType.INCOME);

        assertEquals(1, incomes.size());
        assertEquals(TransactionType.INCOME, incomes.get(0).getType());
    }

    @Test
    void findByCategoryIdShouldFilterTransactions() {
        repository.add(
                new Transaction(
                        TransactionType.EXPENSE,
                        new BigDecimal("25.50"),
                        groceries,
                        september
                )
        );

        repository.add(
                new Transaction(
                        TransactionType.INCOME,
                        new BigDecimal("1200.00"),
                        salary,
                        september
                )
        );

        List<Transaction> groceryTransactions = repository.findByCategoryId(groceries.getId());

        assertEquals(1, groceryTransactions.size());
        assertEquals(groceries, groceryTransactions.get(0).getCategory());
    }

    @Test
    void findByDateBetweenShouldFilterTransactions() {
        repository.add(
                new Transaction(
                        TransactionType.INCOME,
                        new BigDecimal("1200.00"),
                        salary,
                        LocalDate.of(2026, 9, 1)
                )
        );

        repository.add(
                new Transaction(
                        TransactionType.EXPENSE,
                        new BigDecimal("25.50"),
                        groceries,
                        LocalDate.of(2026, 9, 15)
                )
        );

        repository.add(
                new Transaction(
                        TransactionType.EXPENSE,
                        new BigDecimal("40.00"),
                        groceries,
                        LocalDate.of(2026, 10, 1)
                )
        );

        List<Transaction> result = repository.findByDateBetween(
                LocalDate.of(2026, 9, 1),
                LocalDate.of(2026, 9, 30)
        );

        assertEquals(2, result.size());
    }

    @Test
    void findAllSortedByDateDescendingShouldSortTransactions() {
        repository.add(
                new Transaction(
                        TransactionType.INCOME,
                        new BigDecimal("1200.00"),
                        salary,
                        LocalDate.of(2026, 9, 1)
                )
        );

        repository.add(
                new Transaction(
                        TransactionType.EXPENSE,
                        new BigDecimal("25.50"),
                        groceries,
                        LocalDate.of(2026, 9, 12)
                )
        );

        repository.add(
                new Transaction(
                        TransactionType.EXPENSE,
                        new BigDecimal("18.90"),
                        groceries,
                        LocalDate.of(2026, 9, 10)
                )
        );

        List<Transaction> sorted = repository.findAllSortedByDateDescending();

        assertEquals(LocalDate.of(2026, 9, 12), sorted.get(0).getDate());
        assertEquals(LocalDate.of(2026, 9, 10), sorted.get(1).getDate());
        assertEquals(LocalDate.of(2026, 9, 1), sorted.get(2).getDate());
    }

    @Test
    void findCategoryIdsInUseShouldReturnUniqueCategoryIds() {
        repository.add(
                new Transaction(
                        TransactionType.EXPENSE,
                        new BigDecimal("25.50"),
                        groceries,
                        september
                )
        );

        repository.add(
                new Transaction(
                        TransactionType.EXPENSE,
                        new BigDecimal("18.90"),
                        groceries,
                        september
                )
        );

        repository.add(
                new Transaction(
                        TransactionType.INCOME,
                        new BigDecimal("1200.00"),
                        salary,
                        september
                )
        );

        Set<UUID> categoryIds = repository.findCategoryIdsInUse();

        assertEquals(2, categoryIds.size());
        assertTrue(categoryIds.contains(groceries.getId()));
        assertTrue(categoryIds.contains(salary.getId()));
    }
}