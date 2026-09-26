package edu.cssd2101.lab01;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.jupiter.api.BeforeEach;

import static org.junit.jupiter.api.Assertions.*;

public class FixedArrayBookstoreTest{
    private FixedArrayBookstore store;
    private Book bookA;
    private Book bookB;
    private Book bookC;

    @BeforeEach
    void setUp() {
        // Create 3 sample books before each test
        bookA = new Book("111", "Book A", "Author A");
        bookB = new Book("222", "Book B", "Author B");
        bookC = new Book("333", "Book C", "Author C");

        // Start with a store of capacity 3
        store = new FixedArrayBookstore(3);
    }

    // Remove middle, then remaining first and last, then append again
    @Test
    void testRemovalSequenceAndAppend() {
        // Add all books
        store.add(bookA);
        store.add(bookB);
        store.add(bookC);

        // Remove middle item (Book B)
        assertTrue(store.removeByIsbn("222"));
        assertEquals(2, store.size());

        // Remove first item (Book A)
        assertTrue(store.removeByIsbn("111"));
        assertEquals(1, store.size());

        // Remove last item (Book C)
        assertTrue(store.removeByIsbn("333"));
        assertEquals(0, store.size());

        // Add another book
        Book bookD = new Book("444", "Book D", "Author D");
        assertTrue(store.add(bookD));
        assertEquals(1, store.size());
    }

    // Remove missing ISBN Test
    @Test
    void testRemoveMissingIsbnReturnsFalse() {
        store.add(bookA);
        assertFalse(store.removeByIsbn("67")); // Random ISBN which does not exist
        assertEquals(1, store.size());
    }

    // Full capacity Test
    @Test
    void testFullCapacityRules() {
        store.add(bookA);
        store.add(bookB);
        store.add(bookC);

        // If a duplicate is found, it will reuturn false and not throw an exception
        assertFalse(store.add(bookA));

        // Adding a new book is rejected
        Book bookD = new Book("444", "Book D", "Author D");
        assertThrows(IllegalStateException.class, () -> store.add(bookD));

        // Should be able to add after removal
        assertTrue(store.removeByIsbn("222"));
        assertTrue(store.add(bookD));
        assertEquals(3, store.size());
    }

    // Zero Capacity and Negative Capacity Test
    @Test
    void testCapacityEdgeCases() {
        // Zero capacity is permitted but cannot store items
        FixedArrayBookstore zeroStore = new FixedArrayBookstore(0);
        assertThrows(IllegalStateException.class, () -> zeroStore.add(bookA));

        // Negative capacity is rejected
        assertThrows(IllegalArgumentException.class, () -> new FixedArrayBookstore(-1));
    }
}