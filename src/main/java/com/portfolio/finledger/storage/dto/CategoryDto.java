package com.portfolio.finledger.storage.dto;

import java.util.Objects;
import java.util.UUID;

/**
 * Persistence representation of a Category.
 *
 * This DTO is not the domain model.
 * It is only used to save and load data safely.
 */
public record CategoryDto(
        UUID id,
        String name,
        String description
) {

    public CategoryDto {
        Objects.requireNonNull(id, "id must not be null");
        Objects.requireNonNull(name, "name must not be null");

        description = description == null ? "" : description;
    }
}