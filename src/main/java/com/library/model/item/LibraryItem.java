package com.library.model.item;

import com.library.exception.InvalidLoanOperationException;
import com.library.exception.ItemNotAvailableException;
import com.library.util.Validation;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Abstract base class representing any borrowable item in the library catalog.
 * Subclasses define their item-specific metadata and polymorphic loan period in days.
 */
public abstract class LibraryItem {

    private final String id;
    private final String title;
    private boolean available;

    protected LibraryItem(String id, String title) {
        this.id = Validation.requireNonBlank(id, "id");
        this.title = Validation.requireNonBlank(title, "title");
        this.available = true;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public boolean isAvailable() {
        return available;
    }

    /**
     * Returns the loan duration in days for this specific item type.
     */
    public abstract int getLoanPeriodDays();

    /**
     * Calculates the deterministic due date for this item based on the given borrow date
     * and the polymorphic loan period of the concrete item subtype.
     */
    public final LocalDate calculateDueDate(LocalDate borrowDate) {
        Validation.requireNonNull(borrowDate, "borrowDate");
        return borrowDate.plusDays(getLoanPeriodDays());
    }

    /**
     * Marks this item as borrowed (unavailable).
     *
     * @throws ItemNotAvailableException if the item is already checked out
     */
    public void markBorrowed() {
        if (!this.available) {
            throw new ItemNotAvailableException("Item is currently unavailable for borrowing: " + id);
        }
        this.available = false;
    }

    /**
     * Marks this item as returned (available).
     *
     * @throws InvalidLoanOperationException if the item is already available
     */
    public void markReturned() {
        if (this.available) {
            throw new InvalidLoanOperationException("Item is already available and cannot be returned: " + id);
        }
        this.available = true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        LibraryItem that = (LibraryItem) o;
        return available == that.available
                && Objects.equals(id, that.id)
                && Objects.equals(title, that.title);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, available);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName()
                + "{id='" + id + "'"
                + ", title='" + title + "'"
                + ", available=" + available
                + "}";
    }
}
