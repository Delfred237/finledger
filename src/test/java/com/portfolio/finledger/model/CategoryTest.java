package com.portfolio.finledger.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Unit tests for Category.
 *
 * These tests verify construction, validation, and mutation rules.
 */
class CategoryTest {

    @Test
    void createCategoryWithValidNameShouldStoreTrimmedName() {
        Category category = new Category("  Groceries  ");

        assertEquals("Groceries", category.getName());
        assertNotNull(category.getId());
    }

    @Test
    void createCategoryWithMaxLengthNameShouldBeValid() {
        String maxLengthName = "a".repeat(Category.NAME_MAX_LENGTH);

        assertDoesNotThrow(() -> new Category(maxLengthName));
    }

    @Test
    void createCategoryWithNullNameShouldThrow() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new Category(null)
        );
    }

    @Test
    void createCategoryWithBlankNameShouldThrow() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new Category("   ")
        );
    }

    @Test
    void createCategoryWithTooLongNameShouldThrow() {
        String tooLongName = "a".repeat(Category.NAME_MAX_LENGTH + 1);

        assertThrows(
                IllegalArgumentException.class,
                () -> new Category(tooLongName)
        );
    }

    @Test
    void createCategoryWithNullDescriptionShouldStoreEmptyDescription() {
        Category category = new Category("Groceries", null);

        assertEquals("", category.getDescription());
    }

    @Test
    void createCategoryWithDescriptionShouldTrimDescription() {
        Category category = new Category(
                "Groceries",
                "  Food and supermarket  "
        );

        assertEquals("Food and supermarket", category.getDescription());
    }

    @Test
    void renameCategoryWithValidNameShouldUpdateName() {
        Category category = new Category("Groceries");

        category.setName("Transport");

        assertEquals("Transport", category.getName());
    }

    @Test
    void renameCategoryWithBlankNameShouldThrowAndKeepPreviousName() {
        Category category = new Category("Groceries");

        assertThrows(
                IllegalArgumentException.class,
                () -> category.setName(" ")
        );

        assertEquals("Groceries", category.getName());
    }

    @Test
    void setDescriptionWithTooLongDescriptionShouldThrowAndKeepPreviousDescription() {
        Category category = new Category("Groceries");
        String tooLongDescription = "a".repeat(Category.DESCRIPTION_MAX_LENGTH + 1);

        assertThrows(
                IllegalArgumentException.class,
                () -> category.setDescription(tooLongDescription)
        );

        assertEquals("", category.getDescription());
    }
}