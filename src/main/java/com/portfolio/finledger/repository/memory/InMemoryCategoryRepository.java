package com.portfolio.finledger.repository.memory;

import com.portfolio.finledger.model.Category;
import com.portfolio.finledger.repository.CategoryRepository;
import com.portfolio.finledger.exception.DuplicateEntityException;
import com.portfolio.finledger.exception.EntityNotFoundException;
import com.portfolio.finledger.exception.ValidationException;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;

/**
 * In-memory implementation of CategoryRepository.
 *
 * Business rule:
 * category names must be unique, ignoring case and surrounding spaces.
 */
public class InMemoryCategoryRepository extends InMemoryRepository<Category> implements CategoryRepository {

    @Override
    public void add(Category category) {
        requireNonNullEntity(category);

        if (existsByName(category.getName())) {
            throw new DuplicateEntityException(
                    "A category with this name already exists: " + category.getName()
            );
        }

        super.add(category);
    }

    @Override
    public void update(Category category) {
        requireNonNullEntity(category);

        if (!existsById(category.getId())) {
            throw new EntityNotFoundException("Category does not exist: " + category.getId());
        }

        if (existsByNameExcludingId(category.getName(), category.getId())) {
            throw new DuplicateEntityException(
                    "Another category with this name already exists: " + category.getName()
            );
        }

        super.update(category);
    }

    @Override
    public Optional<Category> findByName(String name) {
        if (name == null || name.isBlank()) {
            return Optional.empty();
        }

        String normalizedName = normalizeName(name);

        for (Category category : entities.values()) {
            if (normalizeName(category.getName()).equals(normalizedName)) {
                return Optional.of(category);
            }
        }

        return Optional.empty();
    }

    @Override
    public boolean existsByName(String name) {
        if (name == null || name.isBlank()) {
            return false;
        }

        String normalizedName = normalizeName(name);

        for (Category category : entities.values()) {
            if (normalizeName(category.getName()).equals(normalizedName)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public List<Category> findAllOrderByName() {
        List<Category> result = new ArrayList<>(entities.values());

        result.sort(
                Comparator.comparing(Category::getName, String.CASE_INSENSITIVE_ORDER)
        );

        return List.copyOf(result);
    }

    @Override
    public void rename(UUID id, String newName) {
        if (id == null) {
            throw new ValidationException("id", "Category id must not be null.");
        }

        if (newName == null || newName.isBlank()) {
            throw new ValidationException("name", "Category name must not be blank.");
        }

        Category category = entities.get(id);
        if (category == null) {
            throw new EntityNotFoundException("Category does not exist: " + id);
        }

        if (existsByNameExcludingId(newName, id)) {
            throw new DuplicateEntityException(
                    "Another category with this name already exists: " + newName
            );
        }

        category.setName(newName);
    }

    private boolean existsByNameExcludingId(String name, UUID excludedId) {
        if (name == null || name.isBlank()) {
            return false;
        }

        String normalizedName = normalizeName(name);

        for (Category category : entities.values()) {
            boolean sameId = category.getId().equals(excludedId);
            boolean sameName = normalizeName(category.getName()).equals(normalizedName);

            if (!sameId && sameName) {
                return true;
            }
        }

        return false;
    }

    private static String normalizeName(String name) {
        return name.trim().toLowerCase(Locale.ROOT);
    }
}