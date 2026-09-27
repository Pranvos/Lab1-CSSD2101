package edu.cssd2101.lab01;

import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;

class BookstoreAPITest {

    // Creates two empty bookstores for each test.
    private BookstoreAPI[] createStores() {
        return new BookstoreAPI[] {
            new ArrayListBookstore(),
            new FixedArrayBookstore(10)
        };
    }

    @Test
    void findsBooksByAuthor() {
        for (BookstoreAPI bookstore : createStores()) {
            Book first = new Book(
                "9780134685991", "Effective Java",
                "Joshua Bloch", 100, 2018
            );
            Book second = new Book(
                "9780132350884", "Clean Code",
                "Robert C. Martin", 200, 2008
            );
            Book third = new Book(
                "123456789X", "Java Notes",
                "Joshua Bloch", 300, 2020
            );

            bookstore.add(first);
            bookstore.add(second);
            bookstore.add(third);

            // Checks uppercase input, spaces, and insertion order.
            assertEquals(
                List.of(first, third),
                bookstore.findByAuthor("  BLOCH  ")
            );

            // An unknown author should give an empty result.
            assertTrue(bookstore.findByAuthor("Unknown").isEmpty());
        }
    }

    @Test
    void rejectsInvalidAuthorQueries() {
        for (BookstoreAPI bookstore : createStores()) {
            assertThrows(NullPointerException.class, () ->
                bookstore.findByAuthor(null)
            );

            String[] blanks = {"", "   ", "\t\n"};

            for (String blank : blanks) {
                assertThrows(IllegalArgumentException.class, () ->
                    bookstore.findByAuthor(blank)
                );
            }
        }
    }

    @Test
    void authorSearchWorksWithDifferentLanguageSettings() {
        Locale original = Locale.getDefault();

        try {
            // Turkish has different lowercase rules for the letter I.
            Locale.setDefault(Locale.forLanguageTag("tr-TR"));

            for (BookstoreAPI bookstore : createStores()) {
                Book book = new Book(
                    "9780134685991", "Sample Book",
                    "IRIS", 100, 2020
                );
                bookstore.add(book);

                // Locale.ROOT should allow this match.
                assertEquals(
                    List.of(book),
                    bookstore.findByAuthor("iris")
                );
            }
        } finally {
            // Restore the setting even if the test fails.
            Locale.setDefault(original);
        }
    }

    @Test
    void findsBooksWithinInclusivePriceRange() {
        for (BookstoreAPI bookstore : createStores()) {
            Book first = new Book(
                "9780134685991", "First Book", "Author", 300, 2020
            );
            Book second = new Book(
                "9780132350884", "Second Book", "Author", 100, 2020
            );
            Book third = new Book(
                "123456789X", "Third Book", "Author", 500, 2020
            );

            bookstore.add(first);
            bookstore.add(second);
            bookstore.add(third);

            // Includes both boundaries and preserves insertion order.
            assertEquals(
                List.of(first, second),
                bookstore.findByPriceRange(100, 300)
            );

            // Minimum and maximum can be equal.
            assertEquals(
                List.of(first),
                bookstore.findByPriceRange(300, 300)
            );

            // No books have a price in this range.
            assertTrue(bookstore.findByPriceRange(301, 499).isEmpty());
        }
    }

    @Test
    void rejectsInvalidPriceRanges() {
        for (BookstoreAPI bookstore : createStores()) {
            Book book = new Book(
                "9780134685991", "Sample Book", "Author", 100, 2020
            );
            bookstore.add(book);

            // Minimum cannot be negative.
            assertThrows(IllegalArgumentException.class, () ->
                bookstore.findByPriceRange(-1, 300)
            );

            // Maximum cannot be below minimum.
            assertThrows(IllegalArgumentException.class, () ->
                bookstore.findByPriceRange(300, 100)
            );

            // Invalid searches must not change the store.
            assertEquals(List.of(book), bookstore.allBooks());
        }
    }

    @Test
    void searchResultsAreUnmodifiableAndDetached() {
        for (BookstoreAPI bookstore : createStores()) {
            Book first = new Book(
                "9780134685991", "First Book",
                "Joshua Bloch", 100, 2020
            );
            bookstore.add(first);

            List<Book> authorResults = bookstore.findByAuthor("Bloch");
            List<Book> priceResults = bookstore.findByPriceRange(0, 300);

            // Returned lists cannot be modified.
            assertThrows(UnsupportedOperationException.class, () ->
                authorResults.clear()
            );
            assertThrows(UnsupportedOperationException.class, () ->
                priceResults.clear()
            );

            Book second = new Book(
                "9780132350884", "Second Book",
                "Joshua Bloch", 200, 2021
            );
            bookstore.add(second);

            // Earlier results should not change after adding another book.
            assertEquals(List.of(first), authorResults);
            assertEquals(List.of(first), priceResults);
            assertEquals(List.of(first, second), bookstore.allBooks());
        }
    }

    @Test
    void calculatesInventoryValueCents() {
        for (BookstoreAPI bookstore : createStores()) {
            Book first = new Book(
                "9780134685991", "First Book", "Author", 100, 2020
            );
            Book second = new Book(
                "9780132350884", "Second Book", "Author", 250, 2021
            );

            bookstore.add(first);
            bookstore.add(second);

            // 100 + 250 = 350 cents.
            assertEquals(350L, bookstore.inventoryValueCents());

            // Calculating the total must not change the store.
            assertEquals(List.of(first, second), bookstore.allBooks());
        }
    }

    @Test
    void inventoryValueRejectsOverflow() {
        for (BookstoreAPI bookstore : createStores()) {
            Book first = new Book(
                "9780134685991", "Expensive Book",
                "Author", Long.MAX_VALUE, 2020
            );
            Book second = new Book(
                "9780132350884", "Extra Book", "Author", 1, 2021
            );

            bookstore.add(first);

            // The maximum long value itself is valid.
            assertEquals(
                Long.MAX_VALUE,
                bookstore.inventoryValueCents()
            );

            bookstore.add(second);

            // One more cent exceeds the maximum long value.
            assertThrows(ArithmeticException.class, () ->
                bookstore.inventoryValueCents()
            );

            // The store should remain unchanged after the exception.
            assertEquals(2, bookstore.size());
            assertSame(first, bookstore.allBooks().get(0));
            assertSame(second, bookstore.allBooks().get(1));
        }
    }

    @Test
    void findsMostExpensiveAndKeepsFirstOnTie() {
        for (BookstoreAPI bookstore : createStores()) {
            Book first = new Book(
                "9780134685991", "Cheap Book", "Author", 100, 2020
            );
            Book second = new Book(
                "9780132350884", "Expensive Book", "Author", 500, 2021
            );
            Book third = new Book(
                "123456789X", "Another Expensive Book",
                "Author", 500, 2022
            );

            bookstore.add(first);
            bookstore.add(second);
            bookstore.add(third);

            // Both cost 500, but second was inserted before third.
            assertSame(second, bookstore.mostExpensive().orElseThrow());

            assertEquals(
                List.of(first, second, third),
                bookstore.allBooks()
            );
        }
    }

    @Test
    void findsMostRecentAndKeepsFirstOnTie() {
        for (BookstoreAPI bookstore : createStores()) {
            Book first = new Book(
                "9780134685991", "Older Book", "Author", 500, 2010
            );
            Book second = new Book(
                "9780132350884", "Recent Book", "Author", 100, 2025
            );
            Book third = new Book(
                "123456789X", "Another Recent Book",
                "Author", 200, 2025
            );

            bookstore.add(first);
            bookstore.add(second);
            bookstore.add(third);

            // Both are from 2025, but second was inserted before third.
            assertSame(second, bookstore.mostRecent().orElseThrow());

            assertEquals(
                List.of(first, second, third),
                bookstore.allBooks()
            );
        }
    }

    @Test
    void handlesEmptyStore() {
        for (BookstoreAPI bookstore : createStores()) {
            assertTrue(bookstore.findByAuthor("Bloch").isEmpty());
            assertTrue(bookstore.findByPriceRange(0, 500).isEmpty());

            assertEquals(0L, bookstore.inventoryValueCents());

            assertTrue(bookstore.mostExpensive().isEmpty());
            assertTrue(bookstore.mostRecent().isEmpty());
        }
    }

    @Test
    void handlesOneFreeBook() {
        for (BookstoreAPI bookstore : createStores()) {
            Book book = new Book(
                "9780134685991", "Free Book", "Author", 0, 2020
            );
            bookstore.add(book);

            assertEquals(
                List.of(book),
                bookstore.findByPriceRange(0, 0)
            );
            assertEquals(0L, bookstore.inventoryValueCents());

            // Both methods should return the only book.
            assertSame(book, bookstore.mostExpensive().orElseThrow());
            assertSame(book, bookstore.mostRecent().orElseThrow());
        }
    }
}