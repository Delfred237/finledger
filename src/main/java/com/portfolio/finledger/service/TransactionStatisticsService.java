package com.portfolio.finledger.service;

import com.portfolio.finledger.exception.ValidationException;
import com.portfolio.finledger.model.Category;
import com.portfolio.finledger.model.Transaction;
import com.portfolio.finledger.repository.TransactionRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Computes statistics from transactions using streams.
 *
 * This service demonstrates:
 * - filter
 * - map
 * - reduce
 * - groupingBy
 * - sorted
 * - collect
 */
public class TransactionStatisticsService {

    private final TransactionRepository transactionRepository;

    public TransactionStatisticsService(TransactionRepository transactionRepository) {
        this.transactionRepository = Objects.requireNonNull(
                transactionRepository,
                "transactionRepository must not be null"
        );
    }

    /**
     * Returns expenses grouped by category, sorted by total expense descending.
     *
     * @return list of category expenses
     */
    public List<CategoryExpense> getExpensesByCategory() {
        List<Transaction> transactions = transactionRepository.findAll();

        Map<Category, List<Transaction>> expensesByCategory = transactions.stream()
                .filter(transaction -> transaction.getType().isExpense())
                .collect(Collectors.groupingBy(Transaction::getCategory));

        return expensesByCategory.entrySet().stream()
                .map(entry -> createCategoryExpense(entry.getKey(), entry.getValue()))
                .sorted(
                        Comparator.comparing(CategoryExpense::totalExpense)
                                .reversed()
                                .thenComparing(
                                        expense -> expense.category().getName(),
                                        String.CASE_INSENSITIVE_ORDER
                                )
                )
                .toList();
    }

    /**
     * Returns the category with the highest total expense.
     *
     * @return optional top expense category
     */
    public Optional<CategoryExpense> findTopExpenseCategory() {
        return getExpensesByCategory().stream()
                .findFirst();
    }

    /**
     * Returns monthly summaries between two inclusive dates.
     *
     * @param startInclusive start date
     * @param endInclusive end date
     * @return monthly summaries sorted by month
     */
    public List<MonthlySummary> getMonthlyEvolution(
            LocalDate startInclusive,
            LocalDate endInclusive
    ) {
        validateDateRange(startInclusive, endInclusive);

        List<Transaction> transactions = transactionRepository.findByDateBetween(
                startInclusive,
                endInclusive
        );

        Map<YearMonth, List<Transaction>> transactionsByMonth = transactions.stream()
                .collect(Collectors.groupingBy(transaction -> YearMonth.from(transaction.getDate())));

        return transactionsByMonth.entrySet().stream()
                .map(entry -> createMonthlySummary(entry.getKey(), entry.getValue()))
                .sorted(Comparator.comparing(MonthlySummary::month))
                .toList();
    }

    private CategoryExpense createCategoryExpense(
            Category category,
            List<Transaction> transactions
    ) {
        BigDecimal totalExpense = transactions.stream()
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new CategoryExpense(
                category,
                totalExpense,
                transactions.size()
        );
    }

    private MonthlySummary createMonthlySummary(
            YearMonth month,
            List<Transaction> transactions
    ) {
        BigDecimal income = transactions.stream()
                .filter(transaction -> transaction.getType().isIncome())
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal expense = transactions.stream()
                .filter(transaction -> transaction.getType().isExpense())
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal net = income.subtract(expense);

        return new MonthlySummary(
                month,
                income,
                expense,
                net,
                transactions.size()
        );
    }

    private void validateDateRange(LocalDate startInclusive, LocalDate endInclusive) {
        if (startInclusive == null) {
            throw new ValidationException("startDate", "Start date must not be null.");
        }

        if (endInclusive == null) {
            throw new ValidationException("endDate", "End date must not be null.");
        }

        if (startInclusive.isAfter(endInclusive)) {
            throw new ValidationException(
                    "dateRange",
                    "Start date must not be after end date."
            );
        }
    }
}