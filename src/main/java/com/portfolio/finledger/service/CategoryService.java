package com.portfolio.finledger.service;

import com.portfolio.finledger.exception.DuplicateEntityException;
import com.portfolio.finledger.exception.EntityNotFoundException;
import com.portfolio.finledger.model.Category;
import com.portfolio.finledger.repository.CategoryRepository;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

/**
 * Business service for category operations.
 *
 * This service sits between the UI and the repository.
 * It applies business rules and provides meaningful operations.
 */
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = Objects.requireNonNull(
                categoryRepository,
                "categoryRepository must not be null"
        );
    }

    public Category create(String name, String description) {
        Category category = new Category(name, description);
        categoryRepository.add(category);
        return category;
    }

    public List<Category> findAll() {
        return categoryRepository.findAll();
    }

    public List<Category> findAllOrderByName() {
        return categoryRepository.findAllOrderByName();
    }

    public Optional<Category> findById(UUID id) {
        return categoryRepository.findById(id);
    }

    public Optional<Category> findByName(String name) {
        return categoryRepository.findByName(name);
    }

    public Category getById(UUID id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Category not found: " + id));
    }

    public void rename(UUID id, String newName) {
        categoryRepository.rename(id, newName);
    }

    public boolean delete(UUID id) {
        return categoryRepository.deleteById(id);
    }

    public int count() {
        return categoryRepository.count();
    }
}