package com.library.model.loan;

import com.library.exception.InvalidLoanOperationException;
import com.library.model.item.LibraryItem;
import com.library.model.patron.Patron;
import com.library.util.Validation;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Objects;
import java.util.Optional;

/**
 * First-class relationship entity linking one {@link Patron} to one {@link LibraryItem}
 * for a single deterministic borrowing lifecycle.
 */
public final class Loan {

    private final String loanId;
    private final Patron patron;
    private final LibraryItem item;
    private final LocalDate borrowDate;
    private final LocalDate dueDate;
    private LocalDate returnedDate;
    private BigDecimal finalizedFine;

    public Loan(String loanId, Patron patron, LibraryItem item, LocalDate borrowDate) {
        this.loanId = Validation.requireNonBlank(loanId, "loanId");
        this.patron = Validation.requireNonNull(patron, "patron");
        this.item = Validation.requireNonNull(item, "item");
        this.borrowDate = Validation.requireNonNull(borrowDate, "borrowDate");
        this.dueDate = this.item.calculateDueDate(this.borrowDate);
        this.item.markBorrowed();
    }

    public String getLoanId() {
        return loanId;
    }

    public Patron getPatron() {
        return patron;
    }

    public LibraryItem getItem() {
        return item;
    }

    public LocalDate getBorrowDate() {
        return borrowDate;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public Optional<LocalDate> getReturnedDate() {
        return Optional.ofNullable(returnedDate);
    }

    public Optional<BigDecimal> getFinalizedFine() {
        return Optional.ofNullable(finalizedFine);
    }

    public boolean isActive() {
        return returnedDate == null;
    }

    public boolean isReturned() {
        return returnedDate != null;
    }

    /**
     * Calculates overdue days deterministically as of {@code asOfDate}.
     * Once returned, overdue days remain frozen at {@code returnedDate}.
     */
    public long calculateOverdueDays(LocalDate asOfDate) {
        Validation.requireNonNull(asOfDate, "asOfDate");
        if (asOfDate.isBefore(borrowDate)) {
            throw new InvalidLoanOperationException(
                    "Evaluation date " + asOfDate + " cannot be before borrow date " + borrowDate + "."
            );
        }
        LocalDate effectiveDate = isReturned() ? returnedDate : asOfDate;
        if (!effectiveDate.isAfter(dueDate)) {
            return 0L;
        }
        return ChronoUnit.DAYS.between(dueDate, effectiveDate);
    }

    /**
     * Returns whether this loan is overdue as of {@code asOfDate}
     * (or was returned past its due date once finalized).
     */
    public boolean isOverdue(LocalDate asOfDate) {
        return calculateOverdueDays(asOfDate) > 0L;
    }

    /**
     * Calculates the overdue fine as of {@code asOfDate} using the patron's polymorphic
     * daily rate and fine strategy, or returns the frozen finalized fine if already returned.
     */
    public BigDecimal calculateOverdueFine(LocalDate asOfDate) {
        long overdueDays = calculateOverdueDays(asOfDate);
        if (isReturned()) {
            return finalizedFine;
        }
        return patron.calculateOverdueFine(overdueDays);
    }

    /**
     * Finalizes the loan on {@code returnDate}, restores item availability,
     * freezes the calculated overdue fine, and returns the finalized scale-2 fine.
     */
    public BigDecimal markReturned(LocalDate returnDate) {
        Validation.requireNonNull(returnDate, "returnDate");
        if (isReturned()) {
            throw new InvalidLoanOperationException("Loan has already been returned: " + loanId);
        }
        if (returnDate.isBefore(borrowDate)) {
            throw new InvalidLoanOperationException(
                    "Return date " + returnDate + " cannot be before borrow date " + borrowDate + "."
            );
        }
        long overdueDays = returnDate.isAfter(dueDate)
                ? ChronoUnit.DAYS.between(dueDate, returnDate)
                : 0L;
        BigDecimal computedFine = patron.calculateOverdueFine(overdueDays);
        this.item.markReturned();
        this.returnedDate = returnDate;
        this.finalizedFine = computedFine;
        return this.finalizedFine;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Loan loan = (Loan) o;
        return Objects.equals(loanId, loan.loanId)
                && Objects.equals(patron, loan.patron)
                && Objects.equals(item, loan.item)
                && Objects.equals(borrowDate, loan.borrowDate)
                && Objects.equals(dueDate, loan.dueDate)
                && Objects.equals(returnedDate, loan.returnedDate)
                && Objects.equals(finalizedFine, loan.finalizedFine);
    }

    @Override
    public int hashCode() {
        return Objects.hash(loanId, patron, item, borrowDate, dueDate, returnedDate, finalizedFine);
    }

    @Override
    public String toString() {
        return "Loan{loanId='" + loanId + "'"
                + ", patronId='" + patron.getId() + "'"
                + ", itemId='" + item.getId() + "'"
                + ", borrowDate=" + borrowDate
                + ", dueDate=" + dueDate
                + ", returnedDate=" + returnedDate
                + ", finalizedFine=" + finalizedFine
                + "}";
    }
}
