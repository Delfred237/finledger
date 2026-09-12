package com.portfolio.finledger.storage.dto;

import com.portfolio.finledger.model.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

/**
 * Persistence representation of a Transaction.
 *
 * The transaction references its category by id.
 * This avoids duplicating category data inside every transaction.
 */
public record TransactionDto(
        UUID id,
        TransactionType type,
        BigDecimal amount,
        UUID categoryId,
        LocalDate date,
        String description
) {

    public TransactionDto {
        Objects.requireNonNull(id, "id must not be null");
        Objects.requireNonNull(type, "type must not be null");
        Objects.requireNonNull(amount, "amount must not be null");
        Objects.requireNonNull(categoryId, "categoryId must not be null");
        Objects.requireNonNull(date, "date must not be null");

        description = description == null ? "" : description;
    }
}