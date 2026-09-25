package edu.cssd2101.lab01;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/** Fixed-capacity array store: live entries occupy exactly indices zero through size-1. */
public final class FixedArrayBookstore implements BookstoreAPI {
    private final Book[] books;
    private int size;

    /**
     * Creates an empty bounded catalogue.
     *
     * @param capacity maximum entries, including zero
     * @throws IllegalArgumentException if capacity is negative
     */
    public FixedArrayBookstore(int capacity) {
        if (capacity < 0) throw new IllegalArgumentException("capacity must be nonnegative");
        books = new Book[capacity];
    }

    /** {@inheritDoc} */
    @Override
    public boolean add(Book book) {
        Objects.requireNonNull(book, "book");
        for (int i = 0; i < size; i++) if (books[i].equals(book)) return false;
        if (size == books.length) throw new IllegalStateException("catalogue is full");
        books[size++] = book;
        return true;
    }

    /** {@inheritDoc} */
    @Override
    public boolean removeByIsbn(String isbn) {
        // TODO T4: implement the documented extension contract.

        // isbn musn't be null
        if (isbn == null){ throw new NullPointerException("isbn cannot be null"); }

        // Loop through books array to find the indexToRemove
        for(int i = 0; i < size; i++){

            // isbn located at current index
            if(books[i].isbn().equals(isbn)){

                // Loop through remainder of the array, shifting elements to the left
                for(int j = i; j < size - 1; j++){
                    books[j] = books[j + 1];
                }

                // Since we shift objects to the left by 1 by copying the next index
                // We are left with the last object being unchanged
                // We simply set this to null
                books[size - 1] = null;
                size--;
                return true;
            }
        }

        return false;
    }

    /** {@inheritDoc} */
    @Override
    public List<Book> allBooks() {
        return List.copyOf(Arrays.asList(Arrays.copyOf(books, size)));
    }

    /** {@inheritDoc} */
    @Override
    public int size() {
        return size;
    }
}
