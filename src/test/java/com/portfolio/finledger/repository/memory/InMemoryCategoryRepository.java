package com.portfolio.finledger.repository.memory;

import com.portfolio.finledger.model.Category;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class InMemoryCategoryRepositoryTest {

    private InMemoryCategoryRepository repository;

    @BeforeEach
    void setUp() {
        repository = new InMemoryCategoryRepository();
    }

    @Test
    void addShouldStoreCategory() {
        Category category = new Category("Groceries");

        repository.add(category);

        assertEquals(1, repository.count());
        assertTrue(repository.existsById(category.getId()));
    }

    @Test
    void addShouldRejectDuplicateNameIgnoringCase() {
        repository.add(new Category("Groceries"));

        assertThrows(
                IllegalStateException.class,
                () -> repository.add(new Category("  groceries "))
        );
    }

    @Test
    void findByNameShouldReturnCategory() {
        Category category = new Category("Groceries");
        repository.add(category);

        Optional<Category> found = repository.findByName("groceries");

        assertTrue(found.isPresent());
        assertSame(category, found.get());
    }

    @Test
    void findAllOrderByNameShouldSortCategories() {
        repository.add(new Category("Transport"));
        repository.add(new Category("Groceries"));
        repository.add(new Category("Rent"));

        List<Category> categories = repository.findAllOrderByName();

        assertEquals("Groceries", categories.get(0).getName());
        assertEquals("Rent", categories.get(1).getName());
        assertEquals("Transport", categories.get(2).getName());
    }

    @Test
    void renameShouldAllowRenameWhenNameIsUnique() {
        Category category = new Category("Groceries");
        repository.add(category);

        repository.rename(category.getId(), "Food");

        assertEquals(
                "Food",
                repository.findById(category.getId()).orElseThrow().getName()
        );
    }

    @Test
    void renameShouldRejectDuplicateName() {
        repository.add(new Category("Groceries"));

        Category transport = new Category("Transport");
        repository.add(transport);

        assertThrows(
                IllegalStateException.class,
                () -> repository.rename(transport.getId(), "groceries")
        );

        assertEquals(
                "Transport",
                repository.findById(transport.getId()).orElseThrow().getName()
        );
    }

    @Test
    void deleteByIdShouldRemoveCategory() {
        Category category = new Category("Groceries");
        repository.add(category);

        assertTrue(repository.deleteById(category.getId()));

        assertEquals(0, repository.count());
        assertFalse(repository.existsById(category.getId()));
    }
}