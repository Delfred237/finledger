package com.portfolio.finledger.service;

import com.portfolio.finledger.model.Transaction;
import com.portfolio.finledger.repository.TransactionRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Objects;

/**
 * Computes budget summaries from transactions.
 *
 * This service is responsible for business aggregation:
 * - balance
 * - total income
 * - total expense
 * - monthly summary
 */
public class TransactionSummaryService {

    private final TransactionRepository transactionRepository;

    public TransactionSummaryService(TransactionRepository transactionRepository) {
        this.transactionRepository = Objects.requireNonNull(
                transactionRepository,
                "transactionRepository must not be null"
        );
    }

    /**
     * Computes the global dashboard summary.
     *
     * @return dashboard summary
     */
    public DashboardSummary getDashboard() {
        List<Transaction> transactions = transactionRepository.findAll();

        Totals totals = calculateTotals(transactions);
        BigDecimal balance = totals.income().subtract(totals.expense());

        return new DashboardSummary(
                balance,
                totals.income(),
                totals.expense(),
                transactions.size()
        );
    }

    /**
     * Computes the summary for a specific month.
     *
     * @param month requested month
     * @return monthly summary
     */
    public MonthlySummary getMonthlySummary(YearMonth month) {
        Objects.requireNonNull(month, "month must not be null");

        LocalDate startInclusive = month.atDay(1);
        LocalDate endInclusive = month.atEndOfMonth();

        List<Transaction> transactions = transactionRepository.findByDateBetween(
                startInclusive,
                endInclusive
        );

        Totals totals = calculateTotals(transactions);
        BigDecimal net = totals.income().subtract(totals.expense());

        return new MonthlySummary(
                month,
                totals.income(),
                totals.expense(),
                net,
                transactions.size()
        );
    }

    private Totals calculateTotals(List<Transaction> transactions) {
        BigDecimal income = BigDecimal.ZERO;
        BigDecimal expense = BigDecimal.ZERO;

        for (Transaction transaction : transactions) {
            if (transaction.getType().isIncome()) {
                income = income.add(transaction.getAmount());
            } else {
                expense = expense.add(transaction.getAmount());
            }
        }

        return new Totals(income, expense);
    }

    /**
     * Internal holder for income and expense totals.
     */
    private record Totals(BigDecimal income, BigDecimal expense) {
    }
}