package com.portfolio.finledger.repository.memory;

import com.portfolio.finledger.model.Identifiable;
import com.portfolio.finledger.repository.Repository;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * Generic in-memory repository.
 *
 * Data is stored in a LinkedHashMap to preserve insertion order.
 * This implementation is not thread-safe and is not persistent.
 *
 * @param <T> entity type
 */
public abstract class InMemoryRepository<T extends Identifiable> implements Repository<T> {

    protected final Map<UUID, T> entities = new LinkedHashMap<>();

    protected void requireNonNullEntity(T entity) {
        if (entity == null) {
            throw new IllegalArgumentException("Entity must not be null.");
        }
    }

    @Override
    public void add(T entity) {
        requireNonNullEntity(entity);

        UUID id = entity.getId();
        if (id == null) {
            throw new IllegalArgumentException("Entity id must not be null.");
        }

        if (entities.containsKey(id)) {
            throw new IllegalStateException("Entity already exists with id: " + id);
        }

        entities.put(id, entity);
    }

    @Override
    public Optional<T> findById(UUID id) {
        if (id == null) {
            return Optional.empty();
        }

        return Optional.ofNullable(entities.get(id));
    }

    @Override
    public List<T> findAll() {
        return List.copyOf(entities.values());
    }

    @Override
    public boolean existsById(UUID id) {
        if (id == null) {
            return false;
        }

        return entities.containsKey(id);
    }

    @Override
    public void update(T entity) {
        requireNonNullEntity(entity);

        UUID id = entity.getId();
        if (id == null) {
            throw new IllegalArgumentException("Entity id must not be null.");
        }

        if (!entities.containsKey(id)) {
            throw new IllegalStateException("Entity does not exist with id: " + id);
        }

        entities.put(id, entity);
    }

    @Override
    public boolean deleteById(UUID id) {
        if (id == null) {
            return false;
        }

        return entities.remove(id) != null;
    }

    @Override
    public int count() {
        return entities.size();
    }

    @Override
    public void clear() {
        entities.clear();
    }
}