package com.library.model.item;

import com.library.util.Validation;

import java.util.Objects;

/**
 * Represents a book in the library catalog with a 14-day loan period.
 */
public final class Book extends LibraryItem {

    public static final int LOAN_PERIOD_DAYS = 14;

    private final String author;
    private final String isbn;

    public Book(String id, String title, String author, String isbn) {
        super(id, title);
        this.author = Validation.requireNonBlank(author, "author");
        this.isbn = Validation.requireNonBlank(isbn, "isbn");
    }

    public String getAuthor() {
        return author;
    }

    public String getIsbn() {
        return isbn;
    }

    @Override
    public int getLoanPeriodDays() {
        return LOAN_PERIOD_DAYS;
    }

    @Override
    public boolean equals(Object o) {
        if (!super.equals(o)) {
            return false;
        }
        Book book = (Book) o;
        return Objects.equals(author, book.author)
                && Objects.equals(isbn, book.isbn);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), author, isbn);
    }

    @Override
    public String toString() {
        return "Book{id='" + getId() + "'"
                + ", title='" + getTitle() + "'"
                + ", author='" + author + "'"
                + ", isbn='" + isbn + "'"
                + ", available=" + isAvailable()
                + "}";
    }
}
