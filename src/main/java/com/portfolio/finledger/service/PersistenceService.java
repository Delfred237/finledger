package com.portfolio.finledger.service;

import com.portfolio.finledger.exception.StorageException;
import com.portfolio.finledger.model.Category;
import com.portfolio.finledger.model.Transaction;
import com.portfolio.finledger.repository.CategoryRepository;
import com.portfolio.finledger.repository.TransactionRepository;
import com.portfolio.finledger.storage.dto.CategoryDto;
import com.portfolio.finledger.storage.dto.DataSnapshot;
import com.portfolio.finledger.storage.dto.TransactionDto;
import com.portfolio.finledger.storage.json.JsonFileStorage;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

/**
 * Loads and saves application data.
 *
 * This service maps between domain repositories and persistence DTOs.
 */
public class PersistenceService {

    private final CategoryRepository categoryRepository;
    private final TransactionRepository transactionRepository;
    private final JsonFileStorage storage;

    public PersistenceService(
            CategoryRepository categoryRepository,
            TransactionRepository transactionRepository,
            JsonFileStorage storage
    ) {
        this.categoryRepository = Objects.requireNonNull(
                categoryRepository,
                "categoryRepository must not be null"
        );

        this.transactionRepository = Objects.requireNonNull(
                transactionRepository,
                "transactionRepository must not be null"
        );

        this.storage = Objects.requireNonNull(storage, "storage must not be null");
    }

    /**
     * Saves current repository state to storage.
     */
    public void save() {
        List<CategoryDto> categories = categoryRepository.findAll().stream()
                .map(this::toCategoryDto)
                .toList();

        List<TransactionDto> transactions = transactionRepository.findAll().stream()
                .map(this::toTransactionDto)
                .toList();

        storage.save(new DataSnapshot(categories, transactions));
    }

    /**
     * Loads data from storage into repositories.
     *
     * If the storage file does not exist, repositories are cleared.
     */
    public void load() {
        DataSnapshot snapshot = storage.load();

        Map<UUID, Category> restoredCategories = new LinkedHashMap<>();
        Set<String> normalizedCategoryNames = new HashSet<>();

        for (CategoryDto dto : snapshot.categories()) {
            Category category = Category.restore(dto.id(), dto.name(), dto.description());

            Category existing = restoredCategories.putIfAbsent(category.getId(), category);
            if (existing != null) {
                throw new StorageException(
                        "Duplicate category id in snapshot: " + category.getId()
                );
            }

            String normalizedName = normalize(category.getName());
            if (!normalizedCategoryNames.add(normalizedName)) {
                throw new StorageException(
                        "Duplicate category name in snapshot: " + category.getName()
                );
            }
        }

        List<Transaction> restoredTransactions = new ArrayList<>();
        Set<UUID> restoredTransactionIds = new HashSet<>();

        for (TransactionDto dto : snapshot.transactions()) {
            Category category = restoredCategories.get(dto.categoryId());

            if (category == null) {
                throw new StorageException(
                        "Transaction refers to missing category id: " + dto.categoryId()
                );
            }

            Transaction transaction = Transaction.restore(
                    dto.id(),
                    dto.type(),
                    dto.amount(),
                    category,
                    dto.date(),
                    dto.description()
            );

            if (!restoredTransactionIds.add(transaction.getId())) {
                throw new StorageException(
                        "Duplicate transaction id in snapshot: " + transaction.getId()
                );
            }

            restoredTransactions.add(transaction);
        }

        categoryRepository.clear();
        transactionRepository.clear();

        restoredCategories.values().forEach(categoryRepository::add);
        restoredTransactions.forEach(transactionRepository::add);
    }

    private CategoryDto toCategoryDto(Category category) {
        return new CategoryDto(
                category.getId(),
                category.getName(),
                category.getDescription()
        );
    }

    private TransactionDto toTransactionDto(Transaction transaction) {
        return new TransactionDto(
                transaction.getId(),
                transaction.getType(),
                transaction.getAmount(),
                transaction.getCategory().getId(),
                transaction.getDate(),
                transaction.getDescription()
        );
    }

    private static String normalize(String value) {
        return value.trim().toLowerCase(Locale.ROOT);
    }
}