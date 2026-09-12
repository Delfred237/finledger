package com.portfolio.finledger.service;

import com.portfolio.finledger.exception.EntityNotFoundException;
import com.portfolio.finledger.model.Category;
import com.portfolio.finledger.model.Transaction;
import com.portfolio.finledger.model.TransactionType;
import com.portfolio.finledger.repository.TransactionRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

/**
 * Business service for transaction operations.
 */
public class TransactionService {

    private final TransactionRepository transactionRepository;

    public TransactionService(TransactionRepository transactionRepository) {
        this.transactionRepository = Objects.requireNonNull(
                transactionRepository,
                "transactionRepository must not be null"
        );
    }

    public Transaction create(
            TransactionType type,
            BigDecimal amount,
            Category category,
            LocalDate date,
            String description
    ) {
        Transaction transaction = new Transaction(type, amount, category, date, description);
        transactionRepository.add(transaction);
        return transaction;
    }

    public List<Transaction> findAll() {
        return transactionRepository.findAll();
    }

    public List<Transaction> findAllSortedByDateDescending() {
        return transactionRepository.findAllSortedByDateDescending();
    }

    public List<Transaction> findByType(TransactionType type) {
        return transactionRepository.findByType(type);
    }

    public List<Transaction> findByCategoryId(UUID categoryId) {
        return transactionRepository.findByCategoryId(categoryId);
    }

    public List<Transaction> findByDateBetween(LocalDate start, LocalDate end) {
        return transactionRepository.findByDateBetween(start, end);
    }

    public Optional<Transaction> findById(UUID id) {
        return transactionRepository.findById(id);
    }

    public Transaction getById(UUID id) {
        return transactionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Transaction not found: " + id));
    }

    public boolean delete(UUID id) {
        return transactionRepository.deleteById(id);
    }

    public Set<UUID> findCategoryIdsInUse() {
        return transactionRepository.findCategoryIdsInUse();
    }

    public int count() {
        return transactionRepository.count();
    }
}