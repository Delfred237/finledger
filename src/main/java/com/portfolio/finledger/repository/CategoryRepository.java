package com.portfolio.finledger.repository;

import com.portfolio.finledger.model.Category;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository contract for categories.
 */
public interface CategoryRepository extends Repository<Category> {

    Optional<Category> findByName(String name);

    boolean existsByName(String name);

    List<Category> findAllOrderByName();

    void rename(UUID id, String newName);
}