package com.portfolio.finledger.repository;

import com.portfolio.finledger.model.Transaction;
import com.portfolio.finledger.model.TransactionType;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Repository contract for transactions.
 */
public interface TransactionRepository extends Repository<Transaction> {

    List<Transaction> findByType(TransactionType type);

    List<Transaction> findByCategoryId(UUID categoryId);

    List<Transaction> findByDateBetween(LocalDate startInclusive, LocalDate endInclusive);

    List<Transaction> findAllSortedByDateDescending();

    Set<UUID> findCategoryIdsInUse();
}