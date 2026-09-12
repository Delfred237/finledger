package com.portfolio.finledger.service;

import com.portfolio.finledger.exception.StorageException;
import com.portfolio.finledger.model.Category;
import com.portfolio.finledger.model.Transaction;
import com.portfolio.finledger.model.TransactionType;
import com.portfolio.finledger.repository.memory.InMemoryCategoryRepository;
import com.portfolio.finledger.repository.memory.InMemoryTransactionRepository;
import com.portfolio.finledger.storage.dto.DataSnapshot;
import com.portfolio.finledger.storage.dto.TransactionDto;
import com.portfolio.finledger.storage.json.JsonFileStorage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.math.BigDecimal;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PersistenceServiceTest {

    @TempDir
    Path tempDir;

    private Path file;

    private InMemoryCategoryRepository categories;
    private InMemoryTransactionRepository transactions;

    private PersistenceService service;

    @BeforeEach
    void setUp() {
        file = tempDir.resolve("data.json");

        categories = new InMemoryCategoryRepository();
        transactions = new InMemoryTransactionRepository();

        JsonFileStorage storage = new JsonFileStorage(file);
        service = new PersistenceService(categories, transactions, storage);
    }

    private static void assertMoneyEquals(String expected, BigDecimal actual) {
        assertNotNull(actual);
        assertEquals(0, new BigDecimal(expected).compareTo(actual));
    }

    @Test
    void saveAndLoadShouldRestoreCategoriesAndTransactions() {
        Category groceries = new Category("Groceries", "Food");
        categories.add(groceries);

        Transaction transaction = new Transaction(
                TransactionType.EXPENSE,
                new BigDecimal("25.50"),
                groceries,
                LocalDate.of(2026, 9, 12),
                "Supermarket"
        );

        transactions.add(transaction);

        service.save();

        categories.clear();
        transactions.clear();

        service.load();

        assertEquals(1, categories.count());
        assertEquals(1, transactions.count());

        Category loadedCategory = categories.findAll().get(0);
        assertEquals(groceries.getId(), loadedCategory.getId());
        assertEquals("Groceries", loadedCategory.getName());
        assertEquals("Food", loadedCategory.getDescription());

        Transaction loadedTransaction = transactions.findAll().get(0);
        assertEquals(transaction.getId(), loadedTransaction.getId());
        assertEquals(groceries.getId(), loadedTransaction.getCategory().getId());
        assertMoneyEquals("25.50", loadedTransaction.getAmount());
    }

    @Test
    void loadShouldClearRepositoriesWhenFileDoesNotExist() {
        Category temporary = new Category("Temporary");
        categories.add(temporary);

        transactions.add(
                new Transaction(
                        TransactionType.EXPENSE,
                        new BigDecimal("5.00"),
                        temporary,
                        LocalDate.of(2026, 9, 12)
                )
        );

        service.load();

        assertEquals(0, categories.count());
        assertEquals(0, transactions.count());
    }

    @Test
    void loadShouldThrowWhenTransactionReferencesMissingCategory() {
        Path corruptFile = tempDir.resolve("corrupt.json");
        JsonFileStorage corruptStorage = new JsonFileStorage(corruptFile);

        PersistenceService corruptService = new PersistenceService(
                new InMemoryCategoryRepository(),
                new InMemoryTransactionRepository(),
                corruptStorage
        );

        TransactionDto orphanTransaction = new TransactionDto(
                UUID.randomUUID(),
                TransactionType.EXPENSE,
                new BigDecimal("10.00"),
                UUID.randomUUID(),
                LocalDate.of(2026, 9, 12),
                "Orphan transaction"
        );

        corruptStorage.save(
                new DataSnapshot(
                        List.of(),
                        List.of(orphanTransaction)
                )
        );

        assertThrows(
                StorageException.class,
                corruptService::load
        );
    }
}