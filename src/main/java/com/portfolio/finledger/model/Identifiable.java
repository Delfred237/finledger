package com.portfolio.finledger.model;

import java.util.UUID;

/**
 * Contract for domain objects that have a unique identifier.
 *
 * This interface is intentionally small.
 * It will be useful later for generic persistence or lookup logic.
 */
public interface Identifiable {

    /**
     * Returns the unique identifier of the object.
     *
     * @return unique identifier
     */
    UUID getId();
}