package com.portfolio.finledger.tools;

import com.portfolio.finledger.model.Category;
import com.portfolio.finledger.model.Transaction;
import com.portfolio.finledger.model.TransactionType;
import com.portfolio.finledger.repository.memory.InMemoryCategoryRepository;
import com.portfolio.finledger.repository.memory.InMemoryTransactionRepository;
import com.portfolio.finledger.service.PersistenceService;
import com.portfolio.finledger.storage.json.JsonFileStorage;

import java.math.BigDecimal;
import java.nio.file.Path;
import java.time.LocalDate;

/**
 * Development utility to manually demonstrate persistence.
 *
 * This class is in src/test/java because it is not part of the production CLI yet.
 */
public class PersistenceManualDemo {

    public static void main(String[] args) {
        Path file = Path.of("target/manual-demo/finledger-data.json");

        InMemoryCategoryRepository categories = new InMemoryCategoryRepository();
        InMemoryTransactionRepository transactions = new InMemoryTransactionRepository();

        JsonFileStorage storage = new JsonFileStorage(file);
        PersistenceService persistence = new PersistenceService(categories, transactions, storage);

        Category groceries = new Category("Groceries", "Food and supermarket");
        categories.add(groceries);

        transactions.add(
                new Transaction(
                        TransactionType.EXPENSE,
                        new BigDecimal("25.50"),
                        groceries,
                        LocalDate.of(2026, 9, 12),
                        "Supermarket"
                )
        );

        persistence.save();

        System.out.println("Saved data to: " + file.toAbsolutePath());

        categories.clear();
        transactions.clear();

        persistence.load();

        System.out.println("Loaded categories: " + categories.count());
        System.out.println("Loaded transactions: " + transactions.count());
        System.out.println(categories.findAll());
        System.out.println(transactions.findAll());
    }
}