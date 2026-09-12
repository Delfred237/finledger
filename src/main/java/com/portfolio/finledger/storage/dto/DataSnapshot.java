package com.portfolio.finledger.storage.dto;

import java.util.List;

/**
 * Full snapshot of persisted application data.
 */
public record DataSnapshot(
        List<CategoryDto> categories,
        List<TransactionDto> transactions
) {

    public DataSnapshot {
        categories = categories == null ? List.of() : List.copyOf(categories);
        transactions = transactions == null ? List.of() : List.copyOf(transactions);
    }

    public static DataSnapshot empty() {
        return new DataSnapshot(List.of(), List.of());
    }
}