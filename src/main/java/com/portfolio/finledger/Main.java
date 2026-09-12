package com.portfolio.finledger;

import com.portfolio.finledger.repository.CategoryRepository;
import com.portfolio.finledger.repository.TransactionRepository;
import com.portfolio.finledger.repository.memory.InMemoryCategoryRepository;
import com.portfolio.finledger.repository.memory.InMemoryTransactionRepository;
import com.portfolio.finledger.service.CategoryService;
import com.portfolio.finledger.service.PersistenceService;
import com.portfolio.finledger.service.TransactionService;
import com.portfolio.finledger.service.TransactionStatisticsService;
import com.portfolio.finledger.service.TransactionSummaryService;
import com.portfolio.finledger.storage.json.JsonFileStorage;
import com.portfolio.finledger.ui.Cli;
import com.portfolio.finledger.ui.ConsoleReader;

import java.nio.file.Path;
import java.util.Scanner;

/**
 * Application entry point.
 *
 * Responsibilities:
 * - Create repositories
 * - Create storage
 * - Create services
 * - Load persisted data
 * - Launch CLI
 * - Save data on exit
 */
public final class Main {

    private static final Path DATA_FILE = Path.of("data", "finledger-data.json");

    private Main() {
    }

    public static void main(String[] args) {
        // 1. Create repositories
        CategoryRepository categoryRepository = new InMemoryCategoryRepository();
        TransactionRepository transactionRepository = new InMemoryTransactionRepository();

        // 2. Create storage
        JsonFileStorage storage = new JsonFileStorage(DATA_FILE);

        // 3. Create persistence service
        PersistenceService persistenceService = new PersistenceService(
                categoryRepository,
                transactionRepository,
                storage
        );

        // 4. Load persisted data
        try {
            persistenceService.load();
            System.out.println("Data loaded from: " + DATA_FILE.toAbsolutePath());
        } catch (Exception exception) {
            System.out.println("Warning: Could not load data. Starting fresh.");
            System.out.println("Reason: " + exception.getMessage());
        }

        // 5. Create services
        CategoryService categoryService = new CategoryService(categoryRepository);
        TransactionService transactionService = new TransactionService(transactionRepository);
        TransactionSummaryService summaryService = new TransactionSummaryService(transactionRepository);

        // 6. Create UI
        Scanner scanner = new Scanner(System.in);
        ConsoleReader reader = new ConsoleReader(scanner);

        TransactionStatisticsService statisticsService =
                new TransactionStatisticsService(transactionRepository);

        Cli cli = new Cli(
                reader,
                categoryService,
                transactionService,
                summaryService,
                statisticsService
        );

        // 7. Run CLI
        cli.run();

        // 8. Save data on exit
        try {
            persistenceService.save();
            System.out.println("Data saved to: " + DATA_FILE.toAbsolutePath());
        } catch (Exception exception) {
            System.out.println("Error: Could not save data.");
            System.out.println("Reason: " + exception.getMessage());
        }

        // 9. Close scanner
        scanner.close();
    }
}