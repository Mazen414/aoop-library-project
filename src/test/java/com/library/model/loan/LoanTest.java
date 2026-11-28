package com.library.model.loan;

import com.library.exception.InvalidLibraryArgumentException;
import com.library.exception.InvalidLoanOperationException;
import com.library.exception.ItemNotAvailableException;
import com.library.model.item.Book;
import com.library.model.item.Dvd;
import com.library.model.item.LibraryItem;
import com.library.model.item.Magazine;
import com.library.model.patron.Faculty;
import com.library.model.patron.Patron;
import com.library.model.patron.Student;
import com.library.strategy.CappedFineDecorator;
import com.library.strategy.FineCalculationStrategy;
import com.library.strategy.GracePeriodFineDecorator;
import com.library.strategy.LinearFineStrategy;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LoanTest {

    private static final LocalDate BORROW_DATE = LocalDate.of(2026, 5, 1);

    private Patron createStudent() {
        return new Student("P-1", "Alice", "alice@uni.edu", new LinearFineStrategy());
    }

    private Patron createFaculty() {
        return new Faculty("P-2", "Dr. Bob", "bob@uni.edu", new LinearFineStrategy());
    }

    private Book createBook() {
        return new Book("B-1", "Effective Java", "Joshua Bloch", "978-0134685991");
    }

    @Test
    void shouldDeriveFourteenDayDueDateWhenBorrowingBook() {
        // Arrange
        Patron student = createStudent();
        LibraryItem book = createBook();

        // Act
        Loan loan = new Loan("L-1", student, book, BORROW_DATE);

        // Assert
        assertEquals(LocalDate.of(2026, 5, 15), loan.getDueDate());
    }

    @Test
    void shouldDeriveSevenDayDueDateWhenBorrowingDvd() {
        // Arrange
        Patron student = createStudent();
        LibraryItem dvd = new Dvd("D-1", "Inception", "Christopher Nolan", 148);

        // Act
        Loan loan = new Loan("L-1", student, dvd, BORROW_DATE);

        // Assert
        assertEquals(LocalDate.of(2026, 5, 8), loan.getDueDate());
    }

    @Test
    void shouldDeriveThreeDayDueDateWhenBorrowingMagazine() {
        // Arrange
        Patron faculty = createFaculty();
        LibraryItem magazine = new Magazine("M-1", "Java Magazine", 42, "Oracle Press");

        // Act
        Loan loan = new Loan("L-1", faculty, magazine, BORROW_DATE);

        // Assert
        assertEquals(LocalDate.of(2026, 5, 4), loan.getDueDate());
    }

    @Test
    void shouldMarkItemUnavailableAndStartActiveWhenLoanIsCreated() {
        // Arrange
        Patron student = createStudent();
        Book book = createBook();

        // Act
        Loan loan = new Loan("L-1", student, book, BORROW_DATE);

        // Assert
        assertFalse(book.isAvailable());
        assertTrue(loan.isActive());
        assertFalse(loan.isReturned());
        assertTrue(loan.getReturnedDate().isEmpty());
        assertTrue(loan.getFinalizedFine().isEmpty());
    }

    @Test
    void shouldThrowItemNotAvailableExceptionWhenCreatingLoanForAlreadyBorrowedItem() {
        // Arrange
        Patron student = createStudent();
        Book book = createBook();
        book.markBorrowed();

        // Act & Assert
        assertThrows(
                ItemNotAvailableException.class,
                () -> new Loan("L-1", student, book, BORROW_DATE)
        );
    }

    @Test
    void shouldThrowInvalidLibraryArgumentExceptionWhenLoanIdIsBlank() {
        // Arrange
        Patron student = createStudent();
        Book book = createBook();

        // Act & Assert
        assertThrows(
                InvalidLibraryArgumentException.class,
                () -> new Loan("   ", student, book, BORROW_DATE)
        );
        assertTrue(book.isAvailable());
    }

    @Test
    void shouldThrowInvalidLibraryArgumentExceptionWhenPatronIsNull() {
        // Arrange
        Book book = createBook();

        // Act & Assert
        assertThrows(
                InvalidLibraryArgumentException.class,
                () -> new Loan("L-1", null, book, BORROW_DATE)
        );
        assertTrue(book.isAvailable());
    }

    @Test
    void shouldThrowInvalidLibraryArgumentExceptionWhenItemIsNull() {
        // Arrange
        Patron student = createStudent();

        // Act & Assert
        assertThrows(
                InvalidLibraryArgumentException.class,
                () -> new Loan("L-1", student, null, BORROW_DATE)
        );
    }

    @Test
    void shouldThrowInvalidLibraryArgumentExceptionWhenBorrowDateIsNull() {
        // Arrange
        Patron student = createStudent();
        Book book = createBook();

        // Act & Assert
        assertThrows(
                InvalidLibraryArgumentException.class,
                () -> new Loan("L-1", student, book, null)
        );
        assertTrue(book.isAvailable());
    }

    @Test
    void shouldReturnZeroOverdueDaysAndNotOverdueWhenEvaluatedOnBorrowDateBoundary() {
        // Arrange
        Loan loan = new Loan("L-1", createStudent(), createBook(), BORROW_DATE);

        // Act
        long overdueDays = loan.calculateOverdueDays(BORROW_DATE);
        boolean overdue = loan.isOverdue(BORROW_DATE);

        // Assert
        assertEquals(0L, overdueDays);
        assertFalse(overdue);
    }

    @Test
    void shouldReturnZeroOverdueDaysAndZeroFineWhenEvaluatedOnExactDueDateBoundary() {
        // Arrange
        Loan loan = new Loan("L-1", createStudent(), createBook(), BORROW_DATE);
        LocalDate exactDueDate = LocalDate.of(2026, 5, 15);

        // Act
        long overdueDays = loan.calculateOverdueDays(exactDueDate);
        boolean overdue = loan.isOverdue(exactDueDate);
        BigDecimal fine = loan.calculateOverdueFine(exactDueDate);

        // Assert
        assertEquals(0L, overdueDays);
        assertFalse(overdue);
        assertEquals(new BigDecimal("0.00"), fine);
    }

    @Test
    void shouldReturnOneOverdueDayAndStudentDailyFineWhenEvaluatedOneDayAfterDueDate() {
        // Arrange
        Loan loan = new Loan("L-1", createStudent(), createBook(), BORROW_DATE);
        LocalDate oneDayLate = LocalDate.of(2026, 5, 16);

        // Act
        long overdueDays = loan.calculateOverdueDays(oneDayLate);
        boolean overdue = loan.isOverdue(oneDayLate);
        BigDecimal fine = loan.calculateOverdueFine(oneDayLate);

        // Assert
        assertEquals(1L, overdueDays);
        assertTrue(overdue);
        assertEquals(new BigDecimal("0.50"), fine);
    }

    @Test
    void shouldReturnOneOverdueDayAndFacultyDailyFineWhenEvaluatedOneDayAfterDueDate() {
        // Arrange
        Loan loan = new Loan("L-1", createFaculty(), createBook(), BORROW_DATE);
        LocalDate oneDayLate = LocalDate.of(2026, 5, 16);

        // Act
        long overdueDays = loan.calculateOverdueDays(oneDayLate);
        BigDecimal fine = loan.calculateOverdueFine(oneDayLate);

        // Assert
        assertEquals(1L, overdueDays);
        assertEquals(new BigDecimal("0.25"), fine);
    }

    @Test
    void shouldApplyPatronDecoratedFineStrategyWhenCalculatingActiveLoanOverdueFine() {
        // Arrange
        FineCalculationStrategy decorated = new CappedFineDecorator(
                new GracePeriodFineDecorator(new LinearFineStrategy(), 2L),
                new BigDecimal("2.00")
        );
        Patron student = new Student("P-1", "Alice", "alice@uni.edu", decorated);
        Loan loan = new Loan("L-1", student, new Magazine("M-1", "Java Magazine", 1, "Oracle"), BORROW_DATE);
        LocalDate tenDaysLate = loan.getDueDate().plusDays(10);

        // Act
        BigDecimal fine = loan.calculateOverdueFine(tenDaysLate);

        // Assert
        assertEquals(new BigDecimal("2.00"), fine);
    }

    @Test
    void shouldThrowInvalidLibraryArgumentExceptionWhenCalculateOverdueDaysReceivesNullDate() {
        // Arrange
        Loan loan = new Loan("L-1", createStudent(), createBook(), BORROW_DATE);

        // Act & Assert
        assertThrows(
                InvalidLibraryArgumentException.class,
                () -> loan.calculateOverdueDays(null)
        );
    }

    @Test
    void shouldThrowInvalidLoanOperationExceptionWhenCalculateOverdueDaysReceivesDateBeforeBorrowDate() {
        // Arrange
        Loan loan = new Loan("L-1", createStudent(), createBook(), BORROW_DATE);
        LocalDate dayBeforeBorrow = BORROW_DATE.minusDays(1);

        // Act & Assert
        assertThrows(
                InvalidLoanOperationException.class,
                () -> loan.calculateOverdueDays(dayBeforeBorrow)
        );
    }

    @Test
    void shouldThrowInvalidLoanOperationExceptionWhenIsOverdueReceivesDateBeforeBorrowDate() {
        // Arrange
        Loan loan = new Loan("L-1", createStudent(), createBook(), BORROW_DATE);
        LocalDate dayBeforeBorrow = BORROW_DATE.minusDays(1);

        // Act & Assert
        assertThrows(
                InvalidLoanOperationException.class,
                () -> loan.isOverdue(dayBeforeBorrow)
        );
    }

    @Test
    void shouldThrowInvalidLoanOperationExceptionWhenCalculateOverdueFineReceivesDateBeforeBorrowDate() {
        // Arrange
        Loan loan = new Loan("L-1", createStudent(), createBook(), BORROW_DATE);
        LocalDate dayBeforeBorrow = BORROW_DATE.minusDays(1);

        // Act & Assert
        assertThrows(
                InvalidLoanOperationException.class,
                () -> loan.calculateOverdueFine(dayBeforeBorrow)
        );
    }

    @Test
    void shouldReturnZeroFineAndRestoreItemAvailabilityWhenReturnedBeforeDueDate() {
        // Arrange
        Book book = createBook();
        Loan loan = new Loan("L-1", createStudent(), book, BORROW_DATE);
        LocalDate earlyReturn = LocalDate.of(2026, 5, 10);

        // Act
        BigDecimal fine = loan.markReturned(earlyReturn);

        // Assert
        assertEquals(new BigDecimal("0.00"), fine);
        assertTrue(book.isAvailable());
        assertTrue(loan.isReturned());
        assertFalse(loan.isActive());
        assertEquals(Optional.of(earlyReturn), loan.getReturnedDate());
        assertEquals(Optional.of(new BigDecimal("0.00")), loan.getFinalizedFine());
    }

    @Test
    void shouldReturnZeroFineWhenReturnedOnExactDueDateBoundary() {
        // Arrange
        Loan loan = new Loan("L-1", createStudent(), createBook(), BORROW_DATE);
        LocalDate exactDueDate = LocalDate.of(2026, 5, 15);

        // Act
        BigDecimal fine = loan.markReturned(exactDueDate);

        // Assert
        assertEquals(new BigDecimal("0.00"), fine);
    }

    @Test
    void shouldReturnStudentOneDayFineWhenReturnedOneDayAfterDueDate() {
        // Arrange
        Loan loan = new Loan("L-1", createStudent(), createBook(), BORROW_DATE);
        LocalDate oneDayLate = LocalDate.of(2026, 5, 16);

        // Act
        BigDecimal fine = loan.markReturned(oneDayLate);

        // Assert
        assertEquals(new BigDecimal("0.50"), fine);
        assertEquals(Optional.of(new BigDecimal("0.50")), loan.getFinalizedFine());
    }

    @Test
    void shouldReturnFacultyOneDayFineWhenReturnedOneDayAfterDueDate() {
        // Arrange
        Loan loan = new Loan("L-1", createFaculty(), createBook(), BORROW_DATE);
        LocalDate oneDayLate = LocalDate.of(2026, 5, 16);

        // Act
        BigDecimal fine = loan.markReturned(oneDayLate);

        // Assert
        assertEquals(new BigDecimal("0.25"), fine);
        assertEquals(Optional.of(new BigDecimal("0.25")), loan.getFinalizedFine());
    }

    @Test
    void shouldAllowReturningOnSameDayAsBorrowDateBoundary() {
        // Arrange
        Book book = createBook();
        Loan loan = new Loan("L-1", createStudent(), book, BORROW_DATE);

        // Act
        BigDecimal fine = loan.markReturned(BORROW_DATE);

        // Assert
        assertEquals(new BigDecimal("0.00"), fine);
        assertTrue(book.isAvailable());
    }

    @Test
    void shouldFreezeOverdueDaysAndFineAtReturnDateInsteadOfAccruingExtraDays() {
        // Arrange
        Loan loan = new Loan("L-1", createStudent(), createBook(), BORROW_DATE);
        LocalDate twoDaysLate = LocalDate.of(2026, 5, 17);
        loan.markReturned(twoDaysLate);
        LocalDate thirtyDaysLater = LocalDate.of(2026, 6, 16);

        // Act
        long frozenDays = loan.calculateOverdueDays(thirtyDaysLater);
        BigDecimal frozenFine = loan.calculateOverdueFine(thirtyDaysLater);

        // Assert
        assertEquals(2L, frozenDays);
        assertEquals(new BigDecimal("1.00"), frozenFine);
    }

    @Test
    void shouldReportNotOverdueAfterOnTimeReturnEvenWhenEvaluatedPastDueDate() {
        // Arrange
        Loan loan = new Loan("L-1", createStudent(), createBook(), BORROW_DATE);
        loan.markReturned(LocalDate.of(2026, 5, 15));
        LocalDate laterDate = LocalDate.of(2026, 6, 1);

        // Act
        boolean overdue = loan.isOverdue(laterDate);
        BigDecimal fine = loan.calculateOverdueFine(laterDate);

        // Assert
        assertFalse(overdue);
        assertEquals(new BigDecimal("0.00"), fine);
    }

    @Test
    void shouldThrowInvalidLibraryArgumentExceptionWhenMarkReturnedReceivesNullDate() {
        // Arrange
        Book book = createBook();
        Loan loan = new Loan("L-1", createStudent(), book, BORROW_DATE);

        // Act & Assert
        assertThrows(
                InvalidLibraryArgumentException.class,
                () -> loan.markReturned(null)
        );
        assertTrue(loan.isActive());
        assertFalse(book.isAvailable());
    }

    @Test
    void shouldThrowInvalidLoanOperationExceptionWhenMarkReturnedReceivesDateBeforeBorrowDate() {
        // Arrange
        Book book = createBook();
        Loan loan = new Loan("L-1", createStudent(), book, BORROW_DATE);
        LocalDate dayBeforeBorrow = BORROW_DATE.minusDays(1);

        // Act & Assert
        assertThrows(
                InvalidLoanOperationException.class,
                () -> loan.markReturned(dayBeforeBorrow)
        );
        assertTrue(loan.isActive());
        assertFalse(book.isAvailable());
    }

    @Test
    void shouldThrowInvalidLoanOperationExceptionWhenMarkReturnedIsCalledOnAlreadyReturnedLoan() {
        // Arrange
        Loan loan = new Loan("L-1", createStudent(), createBook(), BORROW_DATE);
        loan.markReturned(LocalDate.of(2026, 5, 10));

        // Act & Assert
        assertThrows(
                InvalidLoanOperationException.class,
                () -> loan.markReturned(LocalDate.of(2026, 5, 12))
        );
    }

    @Test
    void shouldConsiderTwoLoansEqualByValueWhenAllFieldsMatch() {
        // Arrange
        Loan first = new Loan("L-1", createStudent(), createBook(), BORROW_DATE);
        Loan second = new Loan("L-1", createStudent(), createBook(), BORROW_DATE);

        // Act & Assert
        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());
    }

    @Test
    void shouldConsiderActiveAndReturnedLoansNotEqual() {
        // Arrange
        Loan active = new Loan("L-1", createStudent(), createBook(), BORROW_DATE);
        Loan returned = new Loan("L-1", createStudent(), createBook(), BORROW_DATE);
        returned.markReturned(LocalDate.of(2026, 5, 15));

        // Act & Assert
        assertNotEquals(active, returned);
    }

    @Test
    void shouldIncludeKeyFieldsInLoanToString() {
        // Arrange
        Loan loan = new Loan("L-1", createStudent(), createBook(), BORROW_DATE);

        // Act
        String description = loan.toString();

        // Assert
        assertTrue(description.contains("L-1")
                && description.contains("P-1")
                && description.contains("B-1")
                && description.contains("2026-05-01")
                && description.contains("2026-05-15"));
    }
}
