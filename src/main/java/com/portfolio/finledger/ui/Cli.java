package com.portfolio.finledger.ui;

import com.portfolio.finledger.exception.ValidationException;
import com.portfolio.finledger.model.Category;
import com.portfolio.finledger.model.Transaction;
import com.portfolio.finledger.model.TransactionType;
import com.portfolio.finledger.service.CategoryService;
import com.portfolio.finledger.service.DashboardSummary;
import com.portfolio.finledger.service.TransactionService;
import com.portfolio.finledger.service.TransactionSummaryService;
import com.portfolio.finledger.exception.DuplicateEntityException;
import com.portfolio.finledger.service.CategoryExpense;
import com.portfolio.finledger.service.MonthlySummary;
import com.portfolio.finledger.service.TransactionStatisticsService;

import java.time.DateTimeException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Command Line Interface for FinLedger.
 *
 * This class is responsible only for user interaction.
 * It delegates all business logic to services.
 */
public class Cli {

    private final ConsoleReader reader;
    private final CategoryService categoryService;
    private final TransactionService transactionService;
    private final TransactionSummaryService summaryService;
    private final TransactionStatisticsService statisticsService;

    private boolean running = true;

    public Cli(
            ConsoleReader reader,
            CategoryService categoryService,
            TransactionService transactionService,
            TransactionSummaryService summaryService,
            TransactionStatisticsService statisticsService
    ) {
        this.reader = reader;
        this.categoryService = categoryService;
        this.transactionService = transactionService;
        this.summaryService = summaryService;
        this.statisticsService = statisticsService;
    }

    /**
     * Main loop of the CLI.
     */
    public void run() {
        System.out.println();
        System.out.println("====================================");
        System.out.println("  FinLedger CLI — Budget Manager");
        System.out.println("====================================");

        while (running) {
            printMainMenu();

            Optional<Integer> choice = reader.readInt("Choice: ");

            if (choice.isEmpty()) {
                System.out.println("Invalid input. Please enter a number.");
                continue;
            }

            handleMainMenuChoice(choice.get());
        }
    }

    private void printMainMenu() {
        System.out.println();
        System.out.println("--- Main Menu ---");
        System.out.println("1. Dashboard");
        System.out.println("2. Transactions");
        System.out.println("3. Categories");
        System.out.println("4. Statistics");
        System.out.println("5. Exit");
    }

    private void handleMainMenuChoice(int choice) {
        switch (choice) {
            case 1 -> showDashboard();
            case 2 -> handleTransactionsMenu();
            case 3 -> handleCategoriesMenu();
            case 4 -> handleStatisticsMenu();
            case 5 -> exit();
            default -> System.out.println("Invalid choice.");
        }
    }

    // ─────────────────────────────────────────────
    // Dashboard
    // ─────────────────────────────────────────────

    private void showDashboard() {
        DashboardSummary dashboard = summaryService.getDashboard();

        System.out.println();
        System.out.println("--- Dashboard ---");
        System.out.printf("Balance:        %s%n", dashboard.balance());
        System.out.printf("Total Income:   %s%n", dashboard.totalIncome());
        System.out.printf("Total Expense:  %s%n", dashboard.totalExpense());
        System.out.printf("Transactions:   %d%n", dashboard.transactionCount());
    }

    // ─────────────────────────────────────────────
    // Transactions
    // ─────────────────────────────────────────────

    private void handleTransactionsMenu() {
        System.out.println();
        System.out.println("--- Transactions ---");
        System.out.println("1. Add transaction");
        System.out.println("2. List transactions");
        System.out.println("3. Back");

        Optional<Integer> choice = reader.readInt("Choice: ");

        if (choice.isEmpty()) {
            System.out.println("Invalid input.");
            return;
        }

        switch (choice.get()) {
            case 1 -> addTransaction();
            case 2 -> listTransactions();
            case 3 -> { /* back to main menu */ }
            default -> System.out.println("Invalid choice.");
        }
    }

    private void addTransaction() {
        System.out.println();
        System.out.println("--- Add Transaction ---");

        // Type
        System.out.println("Type: 1=Income, 2=Expense");
        Optional<Integer> typeChoice = reader.readInt("Type: ");

        TransactionType type;
        if (typeChoice.isPresent() && typeChoice.get() == 1) {
            type = TransactionType.INCOME;
        } else if (typeChoice.isPresent() && typeChoice.get() == 2) {
            type = TransactionType.EXPENSE;
        } else {
            System.out.println("Invalid type.");
            return;
        }

        // Amount
        Optional<BigDecimal> amount = reader.readAmount("Amount: ");
        if (amount.isEmpty()) {
            System.out.println("Invalid amount.");
            return;
        }

        // Category
        List<Category> categories = categoryService.findAllOrderByName();
        if (categories.isEmpty()) {
            System.out.println("No categories available. Please create a category first.");
            return;
        }

        System.out.println("Available categories:");
        for (int i = 0; i < categories.size(); i++) {
            System.out.printf("  %d. %s%n", i + 1, categories.get(i).getName());
        }

        Optional<Integer> categoryIndex = reader.readInt("Category number: ");
        if (categoryIndex.isEmpty() || categoryIndex.get() < 1 || categoryIndex.get() > categories.size()) {
            System.out.println("Invalid category selection.");
            return;
        }

        Category category = categories.get(categoryIndex.get() - 1);

        // Date
        Optional<LocalDate> date = reader.readDate("Date (yyyy-MM-dd, empty for today): ");
        LocalDate transactionDate = date.orElse(LocalDate.now());

        // Description
        String description = reader.readString("Description (optional): ");

        try {
            Transaction transaction = transactionService.create(
                    type,
                    amount.get(),
                    category,
                    transactionDate,
                    description.isEmpty() ? null : description
            );

            System.out.println("Transaction created: " + transaction.getId());
        } catch (ValidationException exception) {
            System.out.println("Error: " + exception.getMessage());
        }
    }

    private void listTransactions() {
        List<Transaction> transactions = transactionService.findAllSortedByDateDescending();

        System.out.println();
        System.out.println("--- Transactions ---");

        if (transactions.isEmpty()) {
            System.out.println("No transactions found.");
            return;
        }

        for (Transaction transaction : transactions) {
            String sign = transaction.getType().isIncome() ? "+" : "-";
            System.out.printf("%s | %s%s | %-20s | %s | %s%n",
                    transaction.getDate(),
                    sign,
                    transaction.getAmount(),
                    transaction.getCategory().getName(),
                    transaction.getType(),
                    transaction.getDescription().isEmpty() ? "" : transaction.getDescription()
            );
        }
    }

    // ─────────────────────────────────────────────
    // Categories
    // ─────────────────────────────────────────────

    private void handleCategoriesMenu() {
        System.out.println();
        System.out.println("--- Categories ---");
        System.out.println("1. Add category");
        System.out.println("2. List categories");
        System.out.println("3. Back");

        Optional<Integer> choice = reader.readInt("Choice: ");

        if (choice.isEmpty()) {
            System.out.println("Invalid input.");
            return;
        }

        switch (choice.get()) {
            case 1 -> addCategory();
            case 2 -> listCategories();
            case 3 -> { /* back to main menu */ }
            default -> System.out.println("Invalid choice.");
        }
    }

    private void addCategory() {
        System.out.println();

        Optional<String> name = reader.readNonEmptyString("Category name: ");
        if (name.isEmpty()) {
            System.out.println("Category name cannot be empty.");
            return;
        }

        String description = reader.readString("Description (optional): ");

        try {
            Category category = categoryService.create(
                    name.get(),
                    description.isEmpty() ? null : description
            );

            System.out.println("Category created: " + category.getName());
        } catch (DuplicateEntityException | ValidationException exception) {
            System.out.println("Error: " + exception.getMessage());
        } catch (Exception exception) {
            System.out.println("Error: " + exception.getMessage());
        }
    }

    private void listCategories() {
        List<Category> categories = categoryService.findAllOrderByName();

        System.out.println();
        System.out.println("--- Categories ---");

        if (categories.isEmpty()) {
            System.out.println("No categories found.");
            return;
        }

        for (Category category : categories) {
            System.out.printf("- %s%s%n",
                    category.getName(),
                    category.getDescription().isEmpty() ? "" : " (" + category.getDescription() + ")"
            );
        }
    }

    // ─────────────────────────────────────────────
// Statistics
// ─────────────────────────────────────────────

    private void handleStatisticsMenu() {
        System.out.println();
        System.out.println("--- Statistics ---");
        System.out.println("1. Expenses by category");
        System.out.println("2. Top expense category");
        System.out.println("3. Monthly evolution");
        System.out.println("4. Back");

        Optional<Integer> choice = reader.readInt("Choice: ");

        if (choice.isEmpty()) {
            System.out.println("Invalid input.");
            return;
        }

        switch (choice.get()) {
            case 1 -> showExpensesByCategory();
            case 2 -> showTopExpenseCategory();
            case 3 -> showMonthlyEvolution();
            case 4 -> { /* back to main menu */ }
            default -> System.out.println("Invalid choice.");
        }
    }

    private void showExpensesByCategory() {
        List<CategoryExpense> expenses = statisticsService.getExpensesByCategory();

        System.out.println();
        System.out.println("--- Expenses by Category ---");

        if (expenses.isEmpty()) {
            System.out.println("No expense found.");
            return;
        }

        System.out.printf("%-20s | %12s | %12s%n", "Category", "Total", "Transactions");
        System.out.println("----------------------------------------------");

        for (CategoryExpense expense : expenses) {
            System.out.printf(
                    "%-20s | %12s | %12d%n",
                    expense.category().getName(),
                    expense.totalExpense(),
                    expense.transactionCount()
            );
        }
    }

    private void showTopExpenseCategory() {
        Optional<CategoryExpense> top = statisticsService.findTopExpenseCategory();

        System.out.println();
        System.out.println("--- Top Expense Category ---");

        if (top.isEmpty()) {
            System.out.println("No expense found.");
            return;
        }

        CategoryExpense expense = top.get();

        System.out.printf(
                "Category: %s | Total: %s | Transactions: %d%n",
                expense.category().getName(),
                expense.totalExpense(),
                expense.transactionCount()
        );
    }

    private void showMonthlyEvolution() {
        Optional<Integer> yearInput = reader.readInt("Year (empty for current year): ");
        int year = yearInput.orElse(LocalDate.now().getYear());

        try {
            LocalDate start = LocalDate.of(year, 1, 1);
            LocalDate end = LocalDate.of(year, 12, 31);

            List<MonthlySummary> summaries = statisticsService.getMonthlyEvolution(start, end);

            System.out.println();
            System.out.println("--- Monthly Evolution " + year + " ---");

            if (summaries.isEmpty()) {
                System.out.println("No transaction found for this year.");
                return;
            }

            System.out.printf(
                    "%-8s | %12s | %12s | %12s | %12s%n",
                    "Month",
                    "Income",
                    "Expense",
                    "Net",
                    "Transactions"
            );

            System.out.println("------------------------------------------------------------");

            for (MonthlySummary summary : summaries) {
                System.out.printf(
                        "%-8s | %12s | %12s | %12s | %12d%n",
                        summary.month(),
                        summary.income(),
                        summary.expense(),
                        summary.net(),
                        summary.transactionCount()
                );
            }
        } catch (DateTimeException | ValidationException exception) {
            System.out.println("Error: " + exception.getMessage());
        }
    }

    // ─────────────────────────────────────────────
    // Exit
    // ─────────────────────────────────────────────

    private void exit() {
        System.out.println("Exiting FinLedger. Saving data...");
        running = false;
    }
}