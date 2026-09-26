package edu.cssd2101.lab01;

import org.junit.jupiter.api.Test;
import java.util.TreeSet;

import static org.junit.jupiter.api.Assertions.*;

class BookT1Test {

    @Test
    void normalizesIsbnAndText() {
        // object contains an ISBN (978-0-13-468599-1), a title ("Effective Java"), and an author ("Joshua Bloch") with leading and trailing whitespace.
        // it also contains price in cents and publication year
        // there's whitespace in the declaration to make sure that the method removes the trailing and leading whitespace
        Book book = new Book(
            " 978-0-13-468599-1 ",
            " Effective Java ",
            " Joshua Bloch ",
            4599,
            2018
        );

        // checks if the isbn method works, if they match then the test passes, otherwise it fails
        assertEquals("9780134685991", book.isbn());
        // checks if the title method works, if they match then the test passes, otherwise it fails
        assertEquals("Effective Java", book.title());
        // checks if the author method works, if they match then the test passes, otherwise it fails
        assertEquals("Joshua Bloch", book.author());
    }

    @Test
    // 2 different kinds of valid ISBNs, 10 digit and 9 digit followed by and uppercase X
    void acceptsTenCharacterIsbns() {
        // regular 10 digit
        Book digits = new Book(
            "0132350882", "Book One", "Author", 100, 2020
        );

        // 9 digit with uppercase X
        Book endingWithX = new Book(
            "123456789X", "Book Two", "Author", 100, 2020
        );

        // same check as earlier, checks if the isbn method works with 9 digits and uppercase X
        assertEquals("0132350882", digits.isbn());
        assertEquals("123456789X", endingWithX.isbn());
    }

    @Test
    void acceptsZeroAndMaximumPrices() {
        // price set to 0; the minimum price allowed
        Book free = new Book(
            "0132350882", "Free Book", "Author", 0, 2020
        );

        // price set to the largest possible value a Java long can store
        Book maximum = new Book(
            "0132350882", "Expensive Book", "Author",
            Long.MAX_VALUE, 2020
        );

        assertEquals(0, free.priceCents());
        // checks that max price is stored without being changed or trimmed
        assertEquals(Long.MAX_VALUE, maximum.priceCents());
    }

    @Test
    void rejectsNegativePrice() {
        // makes sure that an illegal argument exception is thrown when a price is set to a negative number
        assertThrows(IllegalArgumentException.class, () ->
            new Book("0132350882", "Title", "Author", -1, 2020)
        );
    }

    @Test
    void acceptsYearEndpoints() {
        // year is set to the oldest possible value
        Book oldest = new Book(
            "0132350882", "Old Book", "Author", 100, 1450
        );

        // year set to newest possible value
        Book newest = new Book(
            "0132350882", "New Book", "Author", 100, 2027
        );

        // same check as before
        assertEquals(1450, oldest.year());
        assertEquals(2027, newest.year());
    }

    @Test
    void rejectsAdjacentInvalidYears() {
        // illegal argument exception if the year is set to 1449, one year below the earliest possible year value
        assertThrows(IllegalArgumentException.class, () ->
            new Book("0132350882", "Title", "Author", 100, 1449)
        );

        // illegal argument exception if the year is set to 2028, one year above the latest possible year value
        assertThrows(IllegalArgumentException.class, () ->
            new Book("0132350882", "Title", "Author", 100, 2028)
        );
    }

    @Test
    void rejectsNullTextAndIsbn() {
        // illegal argument exception if either the isbn, title, or author is null
        assertThrows(NullPointerException.class, () ->
            new Book(null, "Title", "Author", 100, 2020)
        );

        assertThrows(NullPointerException.class, () ->
            new Book("0132350882", null, "Author", 100, 2020)
        );

        assertThrows(NullPointerException.class, () ->
            new Book("0132350882", "Title", null, 100, 2020)
        );
    }

    @Test
    void rejectsBlankTitleAndAuthor() {
        // declare an array of all forms of blank string
        String[] blanks = {"", "   ", "\t\n"};

        // for each blank string, check that an illegal argument exception is thrown when the title or author is set to a blank string
        for (String blank : blanks) {
            assertThrows(IllegalArgumentException.class, () ->
                new Book("0132350882", blank, "Author", 100, 2020)
            );

            assertThrows(IllegalArgumentException.class, () ->
                new Book("0132350882", "Title", blank, 100, 2020)
            );
        }
    }

    @Test
    void rejectsMalformedIsbns() {
        // array of invalid isbns
        String[] invalidIsbns = {
            "",
            "   ",
            "123",
            "123456789012",
            "12345678901234",
            "123456789x",
            "abcdefghij",
            "978/0134685991",
            "978 0134685991"
        };

        // loop through each invalid isbn and check that an illegal argument exception is thrown when the isbn is set to an invalid value
        for (String isbn : invalidIsbns) {
            assertThrows(IllegalArgumentException.class, () ->
                new Book(isbn, "Title", "Author", 100, 2020)
            );
        }
    }

    @Test
    // this test checks that a books ID relies only on its normalized ISBN
    void sameNormalizedIsbnDefinesIdentity() {
        // same isbn but with different formatting, title, author, price, and year
        Book first = new Book(
            " 978-0-13-468599-1 ",
            "First Title", "First Author", 100, 2020
        );

        Book second = new Book(
            "9780134685991",
            "Different Title", "Different Author", 900, 2025
        );

        // comparing the two books, they should be equal because they have the same normalized ISBN
        assertEquals(first, second);
        assertEquals(second, first);
        // matching hash codes
        assertEquals(first.hashCode(), second.hashCode());
        // using compareTo will return 0 if the two books are equal
        assertEquals(0, first.compareTo(second));
        assertEquals(0, second.compareTo(first));
    }

    @Test
    void differentIsbnsWithSameTitleRemainInTreeSet() {
        // identical books, different ISBNs
        Book first = new Book(
            "0132350882", "Same Title", "Author", 100, 2020
        );

        Book second = new Book(
            "9780134685991", "Same Title", "Author", 100, 2020
        );

        // empty set to store the books, using a TreeSet to ensure that the books are sorted and unique
        TreeSet<Book> books = new TreeSet<>();
        books.add(first);
        books.add(second);

        // assertNotEquals checks that the two books are not equal, because they have different ISBNs
        assertNotEquals(first, second);
        // checks that both books are in the set
        assertEquals(2, books.size());
        assertTrue(books.contains(first));
        assertTrue(books.contains(second));
    }

    @Test
    void naturalOrderUsesIsbnRatherThanTitle() {
        // this one should come first
        Book first = new Book(
            "0132350882", "Zulu", "Author", 100, 2020
        );

        Book second = new Book(
            "9780134685991", "Alpha", "Author", 100, 2020
        );

        // checks to see if compareTo works and is ordering the books by ISBN
        assertTrue(first.compareTo(second) < 0);
        assertTrue(second.compareTo(first) > 0);
    }
}