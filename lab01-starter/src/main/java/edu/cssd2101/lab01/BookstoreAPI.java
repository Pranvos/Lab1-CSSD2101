package edu.cssd2101.lab01;

import java.util.List;
import java.util.Optional;

/**
 * Insertion-ordered, ISBN-unique catalogue; implementations are not thread-safe.
 *
 * <p>Null arguments are rejected. Invalid ISBN syntax and blank search text are rejected rather
 * than treated as misses. Snapshots are detached. A duplicate add preserves the original entry.
 * This is a catalogue, not a stock counter.
 */
public interface BookstoreAPI {
    /**
     * Adds an entry if absent. Duplicate detection is O(n).
     *
     * @param book nonnull entry
     * @return true if added, false for an existing ISBN
     * @throws NullPointerException if book is null
     * @throws IllegalStateException if a fixed store has no free slot for a new ISBN
     */
    boolean add(Book book);

    /**
     * Removes the matching entry, preserving survivor order, in O(n).
     *
     * @param isbn syntactically valid ISBN
     * @return true if removed
     * @throws NullPointerException if isbn is null
     * @throws IllegalArgumentException if ISBN syntax is invalid
     */
    boolean removeByIsbn(String isbn);

    /**
     * Looks up identity in O(n).
     *
     * @param isbn syntactically valid ISBN
     * @return matching entry or empty
     * @throws NullPointerException if isbn is null
     * @throws IllegalArgumentException if syntax is invalid
     */
    default Optional<Book> findByIsbn(String isbn) {
        String key = Book.normalizeIsbn(isbn);
        return allBooks().stream().filter(b -> b.isbn().equals(key)).findFirst();
    }

    /**
     * Returns an immutable detached snapshot in insertion order, in O(n).
     *
     * @return nonnull list containing no nulls
     */
    List<Book> allBooks();

    /**
     * Counts stored entries in O(1).
     *
     * @return number of entries
     */
    int size();

    /**
     * Returns an independent, compact array in O(n).
     *
     * @return mutable detached array
     */
    default Book[] snapshotArray() {
        return allBooks().toArray(Book[]::new);
    }

    /**
     * Searches case-insensitive title substrings, in O(n) apart from text length.
     *
     * @param query nonblank text, stripped and normalized using Locale.ROOT
     * @return immutable matching snapshot in insertion order
     * @throws NullPointerException if query is null
     * @throws IllegalArgumentException if query is blank
     */
    default List<Book> findByTitle(String query) {
        String key = Book.text(query).toLowerCase(java.util.Locale.ROOT);
        return allBooks().stream()
                .filter(b -> b.title().toLowerCase(java.util.Locale.ROOT).contains(key))
                .toList();
    }

    /**
     * Searches case-insensitive author substrings in O(n).
     *
     * @param query nonblank query
     * @return immutable matching snapshot in insertion order
     * @throws NullPointerException if query is null
     * @throws IllegalArgumentException if query is blank
     */
    default List<Book> findByAuthor(String query) {
        // John - Finished
        String key = Book.text(query).toLowerCase(java.util.Locale.ROOT);
        return allBooks().stream().filter(b -> b.author().toLowerCase(java.util.Locale.ROOT).contains(key)).toList();
    }

    /**
     * Finds entries within inclusive cent bounds in O(n).
     *
     * @param minimum nonnegative lower bound
     * @param maximum upper bound at least minimum
     * @return immutable matches in insertion order
     * @throws IllegalArgumentException if bounds are invalid
     */
    default List<Book> findByPriceRange(long minimum, long maximum) {
        // John - Finished
        if (minimum < 0 || maximum < minimum) {
            throw new IllegalArgumentException("Invalid price range");
        }
        return allBooks().stream().filter(b -> b.priceCents() >= minimum && b.priceCents() <= maximum).toList();
    }

    /**
     * Finds entries by exact year in O(n); any integer query is permitted.
     *
     * @param year requested year
     * @return immutable matches in insertion order
     */
    default List<Book> findByYear(int year) {
        return allBooks().stream().filter(b -> b.year() == year).toList();
    }

    /**
     * Sums exact cents in O(n).
     *
     * @return zero for an empty catalogue
     * @throws ArithmeticException if the total exceeds long range
     */
    default long inventoryValueCents() {
        // John - Finished
        long sum=0;
        for (Book book : allBooks()){
            sum = Math.addExact(sum, book.priceCents());
        }
        return sum;
    }

    /**
     * Finds the highest price, choosing first insertion on ties, in O(n).
     *
     * @return expensive entry or empty
     */
    default Optional<Book> mostExpensive() {
        // John - finished
        Book expensive = null;
        for (Book book : allBooks()) {
            if (expensive == null || book.priceCents() > expensive.priceCents()) {
                expensive = book;
            }
        }
        if (expensive == null) {
            return Optional.empty();
        } 
        else {
            return Optional.of(expensive);
        }
    }
    /**
     * Finds the latest publication, choosing first insertion on ties, in O(n).
     *
     * @return latest entry or empty
     */
    default Optional<Book> mostRecent() {
        // John - Finished
        Book recent = null;
        for (Book book : allBooks()) {
            if (recent == null || book.year() > recent.year()) {
                recent = book;
            }
        }
        if (recent == null) {
            return Optional.empty();
        } 
        else {
            return Optional.of(recent);
        }
    }
}