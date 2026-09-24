package edu.cssd2101.lab01;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Objects;
import java.util.Optional;

/**
 * Pure sparse-array operations except explicitly named in-place sorts.
 *
 * <p>Null array references are rejected; null slots are skipped or sorted last. Filtering is
 * compact and preserves encounter order.
 */
public final class BookArrayUtils {
    private BookArrayUtils() {}

    /**
     * Filters by inclusive maximum with two scans, O(n) time and O(k) result space.
     *
     * @param books nonnull sparse array
     * @param maximum nonnegative price limit in cents
     * @return new compact array
     * @throws NullPointerException if books is null
     * @throws IllegalArgumentException if maximum is negative
     */
    public static Book[] filterPriceAtMost(Book[] books, long maximum) {
        // TODO T5: implement the documented extension contract.
        throw new UnsupportedOperationException("T5 is an exercise");
    }

    /**
     * Counts entries before a year, O(n).
     *
     * @param books nonnull sparse array
     * @param cutoff exclusive upper year
     * @return matching count
     * @throws NullPointerException if books is null
     */
    public static int countBeforeYear(Book[] books, int cutoff) {
        int count = 0;
        for (Book b : Objects.requireNonNull(books)) if (b != null && b.year() < cutoff) count++;
        return count;
    }

    /**
     * Filters a decade beginning on a multiple of ten, O(n).
     *
     * @param books nonnull sparse array
     * @param decade first year, 0..9990 and divisible by ten
     * @return compact snapshot
     * @throws NullPointerException if books is null
     * @throws IllegalArgumentException if decade is invalid
     */
    public static Book[] filterByDecade(Book[] books, int decade) {
        Objects.requireNonNull(books);
        if (decade < 0 || decade > 9990 || decade % 10 != 0)
            throw new IllegalArgumentException("invalid decade");
        return Arrays.stream(books)
                .filter(Objects::nonNull)
                .filter(b -> b.year() >= decade && b.year() < decade + 10)
                .toArray(Book[]::new);
    }

    /**
     * Sorts in place by price, stably with nulls last, O(n log n).
     *
     * @param books nonnull mutable array
     * @throws NullPointerException if books is null
     */
    public static void sortByPrice(Book[] books) {
        Arrays.sort(
                Objects.requireNonNull(books),
                Comparator.nullsLast(Comparator.comparingLong(Book::priceCents)));
    }

    /**
     * Sorts in place by year, stably with nulls last, O(n log n).
     *
     * @param books nonnull mutable array
     * @throws NullPointerException if books is null
     */
    public static void sortByYear(Book[] books) {
        Arrays.sort(
                Objects.requireNonNull(books),
                Comparator.nullsLast(Comparator.comparingInt(Book::year)));
    }

    /**
     * Averages nonnull prices in currency units, rounding once HALF_EVEN to two decimals.
     *
     * @param books nonnull sparse array
     * @return empty if no entries; otherwise exact-decimal rounded average
     * @throws NullPointerException if books is null
     */
    public static Optional<BigDecimal> averagePrice(Book[] books) {
        // TODO T5: implement the documented extension contract.
        throw new UnsupportedOperationException("T5 is an exercise");
    }

    /**
     * Returns first oldest entry, O(n).
     *
     * @param books nonnull sparse array
     * @return oldest entry or empty
     * @throws NullPointerException if books is null
     */
    public static Optional<Book> findOldest(Book[] books) {
        Book oldest = null;
        for (Book b : Objects.requireNonNull(books))
            if (b != null && (oldest == null || b.year() < oldest.year())) oldest = b;
        return Optional.ofNullable(oldest);
    }

    /**
     * Concatenates arrays, preserving null slots and duplicates, O(n+m).
     *
     * @param first nonnull first array
     * @param second nonnull second array
     * @return independent array in first-then-second order
     * @throws NullPointerException if either array is null
     * @throws ArithmeticException if combined length exceeds int range
     */
    public static Book[] merge(Book[] first, Book[] second) {
        Objects.requireNonNull(first);
        Objects.requireNonNull(second);
        Book[] result = Arrays.copyOf(first, Math.addExact(first.length, second.length));
        System.arraycopy(second, 0, result, first.length, second.length);
        return result;
    }

    /**
     * Removes null slots and repeated ISBNs, preserving the first instance, expected O(n).
     *
     * @param books nonnull sparse array
     * @return new compact array
     * @throws NullPointerException if books is null
     */
    public static Book[] removeDuplicates(Book[] books) {
        // TODO T5: implement the documented extension contract.
        throw new UnsupportedOperationException("T5 is an exercise");
    }

    deafult List<Book>

}
