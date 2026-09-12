package com.portfolio.finledger.repository;

import com.portfolio.finledger.model.Identifiable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Generic repository contract for identifiable entities.
 *
 * @param <T> entity type
 */
public interface Repository<T extends Identifiable> {

    void add(T entity);

    Optional<T> findById(UUID id);

    List<T> findAll();

    boolean existsById(UUID id);

    void update(T entity);

    boolean deleteById(UUID id);

    int count();

    void clear();
}