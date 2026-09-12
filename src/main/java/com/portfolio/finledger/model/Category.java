package com.portfolio.finledger.model;

import com.portfolio.finledger.exception.ValidationException;

import java.util.UUID;
import java.util.Objects;

/**
 * Represents a budget category.
 *
 * A category can later be linked to transactions.
 * Examples: Groceries, Rent, Salary, Transport.
 *
 * This class is part of the domain model.
 * It must not contain CLI, persistence, or framework logic.
 */
public final class Category implements Identifiable {

    /**
     * Maximum allowed length for a category name.
     */
    public static final int NAME_MAX_LENGTH = 50;

    /**
     * Maximum allowed length for a category description.
     */
    public static final int DESCRIPTION_MAX_LENGTH = 200;

    /**
     * Unique identifier of the category.
     *
     * UUID is a reasonable choice here because the application
     * has no external database and no central ID generator.
     */
    private final UUID id;

    /**
     * Category name.
     *
     * The name is validated before being stored.
     * It is not final because a category can be renamed later.
     */
    private String name;

    /**
     * Optional description.
     *
     * Null descriptions are normalized to an empty string.
     */
    private String description;

    /**
     * Creates a category with a name and no description.
     *
     * @param name category name, required
     */
    public Category(String name) {
        this(UUID.randomUUID(), name, null);
    }

    /**
     * Creates a category with a name and an optional description.
     *
     * @param name category name, required
     * @param description optional description, nullable
     */
    public Category(String name, String description) {
        this(UUID.randomUUID(), name, description);
    }

    private Category(UUID id, String name, String description) {
        this.id = validateId(id);

        setName(name);
        setDescription(description);
    }

    public static Category restore(UUID id, String name, String description) {
        return new Category(id, name, description);
    }

    private static UUID validateId(UUID id) {
        if (id == null) {
            throw new ValidationException("id", "Category id must not be null.");
        }

        return id;
    }

    @Override
    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    /**
     * Changes the category name.
     *
     * @param name new category name
     */
    public void setName(String name) {
        this.name = validateName(name);
    }

    /**
     * Changes the category description.
     *
     * @param description new description, nullable
     */
    public void setDescription(String description) {
        this.description = normalizeDescription(description);
    }

    /**
     * Validates and normalizes the category name.
     *
     * @param name raw name input
     * @return trimmed and valid name
     */
    private static String validateName(String name) {
        if (name == null) {
            throw new ValidationException("name", "Category name must not be null.");
        }

        String trimmed = name.trim();

        if (trimmed.isEmpty()) {
            throw new ValidationException("name", "Category name must not be blank.");
        }

        if (trimmed.length() > NAME_MAX_LENGTH) {
            throw new ValidationException(
                    "name",
                    "Category name must not exceed " + NAME_MAX_LENGTH + " characters."
            );
        }

        return trimmed;
    }

    /**
     * Validates and normalizes the description.
     *
     * A null description is converted to an empty string.
     *
     * @param description raw description input
     * @return normalized description
     */
    private static String normalizeDescription(String description) {
        if (description == null) {
            return "";
        }

        String trimmed = description.trim();

        if (trimmed.length() > DESCRIPTION_MAX_LENGTH) {
            throw new ValidationException(
                    "description",
                    "Category description must not exceed " + DESCRIPTION_MAX_LENGTH + " characters."
            );
        }

        return trimmed;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }

        if (!(other instanceof Category category)) {
            return false;
        }

        return Objects.equals(id, category.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Category{id=%s, name='%s', description='%s'}"
                .formatted(id, name, description);
    }
}