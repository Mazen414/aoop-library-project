package com.library.model.item;

import com.library.exception.InvalidLibraryArgumentException;
import com.library.exception.InvalidLoanOperationException;
import com.library.exception.ItemNotAvailableException;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LibraryItemTest {

    @Test
    void shouldReturnFourteenDayLoanPeriodWhenItemIsBook() {
        // Arrange
        LibraryItem book = new Book("B-1", "Effective Java", "Joshua Bloch", "978-0134685991");

        // Act
        int loanPeriodDays = book.getLoanPeriodDays();

        // Assert
        assertEquals(14, loanPeriodDays);
    }

    @Test
    void shouldReturnSevenDayLoanPeriodWhenItemIsDvd() {
        // Arrange
        LibraryItem dvd = new Dvd("D-1", "Inception", "Christopher Nolan", 148);

        // Act
        int loanPeriodDays = dvd.getLoanPeriodDays();

        // Assert
        assertEquals(7, loanPeriodDays);
    }

    @Test
    void shouldReturnThreeDayLoanPeriodWhenItemIsMagazine() {
        // Arrange
        LibraryItem magazine = new Magazine("M-1", "Java Magazine", 42, "Oracle Press");

        // Act
        int loanPeriodDays = magazine.getLoanPeriodDays();

        // Assert
        assertEquals(3, loanPeriodDays);
    }

    @Test
    void shouldCalculateDueDateFourteenDaysAfterBorrowDateWhenItemIsBook() {
        // Arrange
        LibraryItem book = new Book("B-1", "Effective Java", "Joshua Bloch", "978-0134685991");
        LocalDate borrowDate = LocalDate.of(2026, 3, 1);

        // Act
        LocalDate dueDate = book.calculateDueDate(borrowDate);

        // Assert
        assertEquals(LocalDate.of(2026, 3, 15), dueDate);
    }

    @Test
    void shouldCalculateDueDateSevenDaysAfterBorrowDateWhenItemIsDvd() {
        // Arrange
        LibraryItem dvd = new Dvd("D-1", "Inception", "Christopher Nolan", 148);
        LocalDate borrowDate = LocalDate.of(2026, 3, 1);

        // Act
        LocalDate dueDate = dvd.calculateDueDate(borrowDate);

        // Assert
        assertEquals(LocalDate.of(2026, 3, 8), dueDate);
    }

    @Test
    void shouldCalculateDueDateAcrossLeapDayBoundaryWhenItemIsMagazine() {
        // Arrange
        LibraryItem magazine = new Magazine("M-1", "Java Magazine", 42, "Oracle Press");
        LocalDate leapYearFeb28 = LocalDate.of(2024, 2, 28);

        // Act
        LocalDate dueDate = magazine.calculateDueDate(leapYearFeb28);

        // Assert
        assertEquals(LocalDate.of(2024, 3, 2), dueDate);
    }

    @Test
    void shouldThrowInvalidLibraryArgumentExceptionWhenCalculateDueDateReceivesNullBorrowDate() {
        // Arrange
        LibraryItem book = new Book("B-1", "Effective Java", "Joshua Bloch", "978-0134685991");

        // Act & Assert
        assertThrows(
                InvalidLibraryArgumentException.class,
                () -> book.calculateDueDate(null)
        );
    }

    @Test
    void shouldMarkItemAsUnavailableWhenMarkBorrowedIsCalledOnAvailableItem() {
        // Arrange
        LibraryItem book = new Book("B-1", "Effective Java", "Joshua Bloch", "978-0134685991");

        // Act
        book.markBorrowed();

        // Assert
        assertFalse(book.isAvailable());
    }

    @Test
    void shouldThrowItemNotAvailableExceptionWhenMarkBorrowedIsCalledOnAlreadyBorrowedItem() {
        // Arrange
        LibraryItem book = new Book("B-1", "Effective Java", "Joshua Bloch", "978-0134685991");
        book.markBorrowed();

        // Act & Assert
        assertThrows(
                ItemNotAvailableException.class,
                book::markBorrowed
        );
    }

    @Test
    void shouldMarkItemAsAvailableWhenMarkReturnedIsCalledOnBorrowedItem() {
        // Arrange
        LibraryItem dvd = new Dvd("D-1", "Inception", "Christopher Nolan", 148);
        dvd.markBorrowed();

        // Act
        dvd.markReturned();

        // Assert
        assertTrue(dvd.isAvailable());
    }

    @Test
    void shouldThrowInvalidLoanOperationExceptionWhenMarkReturnedIsCalledOnAlreadyAvailableItem() {
        // Arrange
        LibraryItem dvd = new Dvd("D-1", "Inception", "Christopher Nolan", 148);

        // Act & Assert
        assertThrows(
                InvalidLoanOperationException.class,
                dvd::markReturned
        );
    }

    @Test
    void shouldThrowInvalidLibraryArgumentExceptionWhenBookIdIsBlank() {
        // Arrange
        String blankId = "   ";

        // Act & Assert
        assertThrows(
                InvalidLibraryArgumentException.class,
                () -> new Book(blankId, "Effective Java", "Joshua Bloch", "978-0134685991")
        );
    }

    @Test
    void shouldThrowInvalidLibraryArgumentExceptionWhenBookTitleIsBlank() {
        // Arrange
        String blankTitle = "";

        // Act & Assert
        assertThrows(
                InvalidLibraryArgumentException.class,
                () -> new Book("B-1", blankTitle, "Joshua Bloch", "978-0134685991")
        );
    }

    @Test
    void shouldThrowInvalidLibraryArgumentExceptionWhenBookAuthorIsBlank() {
        // Arrange
        String blankAuthor = " ";

        // Act & Assert
        assertThrows(
                InvalidLibraryArgumentException.class,
                () -> new Book("B-1", "Effective Java", blankAuthor, "978-0134685991")
        );
    }

    @Test
    void shouldThrowInvalidLibraryArgumentExceptionWhenBookIsbnIsNull() {
        // Arrange
        String nullIsbn = null;

        // Act & Assert
        assertThrows(
                InvalidLibraryArgumentException.class,
                () -> new Book("B-1", "Effective Java", "Joshua Bloch", nullIsbn)
        );
    }

    @Test
    void shouldAcceptSmallestPositiveDurationBoundaryWhenCreatingDvd() {
        // Arrange
        int minimumValidDuration = 1;

        // Act
        Dvd dvd = new Dvd("D-1", "Short Clip", "Christopher Nolan", minimumValidDuration);

        // Assert
        assertEquals(1, dvd.getDurationMinutes());
    }

    @Test
    void shouldThrowInvalidLibraryArgumentExceptionWhenDvdDirectorIsBlank() {
        // Arrange
        String blankDirector = "  ";

        // Act & Assert
        assertThrows(
                InvalidLibraryArgumentException.class,
                () -> new Dvd("D-1", "Inception", blankDirector, 148)
        );
    }

    @Test
    void shouldThrowInvalidLibraryArgumentExceptionWhenDvdDurationIsZeroBoundary() {
        // Arrange
        int zeroDuration = 0;

        // Act & Assert
        assertThrows(
                InvalidLibraryArgumentException.class,
                () -> new Dvd("D-1", "Inception", "Christopher Nolan", zeroDuration)
        );
    }

    @Test
    void shouldThrowInvalidLibraryArgumentExceptionWhenDvdDurationIsNegative() {
        // Arrange
        int negativeDuration = -120;

        // Act & Assert
        assertThrows(
                InvalidLibraryArgumentException.class,
                () -> new Dvd("D-1", "Inception", "Christopher Nolan", negativeDuration)
        );
    }

    @Test
    void shouldAcceptSmallestPositiveIssueNumberBoundaryWhenCreatingMagazine() {
        // Arrange
        int firstIssue = 1;

        // Act
        Magazine magazine = new Magazine("M-1", "Java Magazine", firstIssue, "Oracle Press");

        // Assert
        assertEquals(1, magazine.getIssueNumber());
    }

    @Test
    void shouldThrowInvalidLibraryArgumentExceptionWhenMagazineIssueNumberIsZeroBoundary() {
        // Arrange
        int zeroIssue = 0;

        // Act & Assert
        assertThrows(
                InvalidLibraryArgumentException.class,
                () -> new Magazine("M-1", "Java Magazine", zeroIssue, "Oracle Press")
        );
    }

    @Test
    void shouldThrowInvalidLibraryArgumentExceptionWhenMagazinePublisherIsBlank() {
        // Arrange
        String blankPublisher = " ";

        // Act & Assert
        assertThrows(
                InvalidLibraryArgumentException.class,
                () -> new Magazine("M-1", "Java Magazine", 1, blankPublisher)
        );
    }

    @Test
    void shouldConsiderTwoBooksEqualByValueWhenAllFieldsMatch() {
        // Arrange
        Book first = new Book("B-1", "Effective Java", "Joshua Bloch", "978-0134685991");
        Book second = new Book("B-1", "Effective Java", "Joshua Bloch", "978-0134685991");

        // Act & Assert
        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());
    }

    @Test
    void shouldConsiderTwoBooksNotEqualWhenAvailabilityDiffers() {
        // Arrange
        Book availableBook = new Book("B-1", "Effective Java", "Joshua Bloch", "978-0134685991");
        Book borrowedBook = new Book("B-1", "Effective Java", "Joshua Bloch", "978-0134685991");
        borrowedBook.markBorrowed();

        // Act & Assert
        assertNotEquals(availableBook, borrowedBook);
    }

    @Test
    void shouldConsiderTwoDvdsEqualByValueWhenAllFieldsMatch() {
        // Arrange
        Dvd first = new Dvd("D-1", "Inception", "Christopher Nolan", 148);
        Dvd second = new Dvd("D-1", "Inception", "Christopher Nolan", 148);

        // Act & Assert
        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());
    }

    @Test
    void shouldConsiderTwoMagazinesEqualByValueWhenAllFieldsMatch() {
        // Arrange
        Magazine first = new Magazine("M-1", "Java Magazine", 42, "Oracle Press");
        Magazine second = new Magazine("M-1", "Java Magazine", 42, "Oracle Press");

        // Act & Assert
        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());
    }

    @Test
    void shouldConsiderDifferentItemSubtypesNotEqualEvenWithSameIdAndTitle() {
        // Arrange
        LibraryItem book = new Book("ITEM-1", "Inception", "Christopher Nolan", "978-0000000001");
        LibraryItem dvd = new Dvd("ITEM-1", "Inception", "Christopher Nolan", 148);

        // Act & Assert
        assertNotEquals(book, dvd);
    }

    @Test
    void shouldIncludeSubtypeFieldsInToStringForBookDvdAndMagazine() {
        // Arrange
        Book book = new Book("B-1", "Effective Java", "Joshua Bloch", "978-0134685991");
        Dvd dvd = new Dvd("D-1", "Inception", "Christopher Nolan", 148);
        Magazine magazine = new Magazine("M-1", "Java Magazine", 42, "Oracle Press");

        // Act
        String bookString = book.toString();
        String dvdString = dvd.toString();
        String magazineString = magazine.toString();

        // Assert
        assertTrue(bookString.contains("Joshua Bloch") && bookString.contains("978-0134685991"));
        assertTrue(dvdString.contains("Christopher Nolan") && dvdString.contains("148"));
        assertTrue(magazineString.contains("42") && magazineString.contains("Oracle Press"));
    }
}
