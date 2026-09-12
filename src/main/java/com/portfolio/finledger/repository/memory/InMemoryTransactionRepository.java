package com.portfolio.finledger.repository.memory;

import com.portfolio.finledger.model.Transaction;
import com.portfolio.finledger.model.TransactionType;
import com.portfolio.finledger.repository.TransactionRepository;
import com.portfolio.finledger.exception.ValidationException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * In-memory implementation of TransactionRepository.
 */
public class InMemoryTransactionRepository extends InMemoryRepository<Transaction> implements TransactionRepository {

    @Override
    public List<Transaction> findByType(TransactionType type) {
        if (type == null) {
            throw new ValidationException("type", "Transaction type must not be null.");
        }

        List<Transaction> result = new ArrayList<>();

        for (Transaction transaction : entities.values()) {
            if (transaction.getType() == type) {
                result.add(transaction);
            }
        }

        return List.copyOf(result);
    }

    @Override
    public List<Transaction> findByCategoryId(UUID categoryId) {
        if (categoryId == null) {
            throw new ValidationException("categoryId", "Category id must not be null.");
        }

        List<Transaction> result = new ArrayList<>();

        for (Transaction transaction : entities.values()) {
            if (transaction.getCategory().getId().equals(categoryId)) {
                result.add(transaction);
            }
        }

        return List.copyOf(result);
    }

    @Override
    public List<Transaction> findByDateBetween(LocalDate startInclusive, LocalDate endInclusive) {
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

        List<Transaction> result = new ArrayList<>();

        for (Transaction transaction : entities.values()) {
            LocalDate date = transaction.getDate();

            boolean notBeforeStart = !date.isBefore(startInclusive);
            boolean notAfterEnd = !date.isAfter(endInclusive);

            if (notBeforeStart && notAfterEnd) {
                result.add(transaction);
            }
        }

        return List.copyOf(result);
    }

    @Override
    public List<Transaction> findAllSortedByDateDescending() {
        List<Transaction> result = new ArrayList<>(entities.values());

        result.sort(
                Comparator.comparing(Transaction::getDate)
                        .reversed()
                        .thenComparing(Transaction::getAmount, Comparator.reverseOrder())
                        .thenComparing(Transaction::getId)
        );

        return List.copyOf(result);
    }

    @Override
    public Set<UUID> findCategoryIdsInUse() {
        Set<UUID> categoryIds = new HashSet<>();

        for (Transaction transaction : entities.values()) {
            categoryIds.add(transaction.getCategory().getId());
        }

        return Set.copyOf(categoryIds);
    }
}