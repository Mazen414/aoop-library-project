package com.library.model.patron;

import com.library.exception.InvalidFinePaymentException;
import com.library.exception.InvalidLibraryArgumentException;
import com.library.strategy.CappedFineDecorator;
import com.library.strategy.FineCalculationStrategy;
import com.library.strategy.GracePeriodFineDecorator;
import com.library.strategy.LinearFineStrategy;
import com.library.strategy.TieredFineStrategy;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PatronTest {

    private static final LocalDate PAYMENT_DATE = LocalDate.of(2026, 4, 20);

    @Test
    void shouldReturnStudentBorrowingLimitAndDailyOverdueFinePolymorphically() {
        // Arrange
        Patron student = new Student("P-1", "Alice", "alice@uni.edu", new LinearFineStrategy());

        // Act
        int limit = student.getBorrowingLimit();
        BigDecimal dailyFine = student.getDailyOverdueFine();

        // Assert
        assertEquals(3, limit);
        assertEquals(new BigDecimal("0.50"), dailyFine);
    }

    @Test
    void shouldReturnFacultyBorrowingLimitAndDailyOverdueFinePolymorphically() {
        // Arrange
        Patron faculty = new Faculty("P-2", "Dr. Bob", "bob@uni.edu", new LinearFineStrategy());

        // Act
        int limit = faculty.getBorrowingLimit();
        BigDecimal dailyFine = faculty.getDailyOverdueFine();

        // Assert
        assertEquals(10, limit);
        assertEquals(new BigDecimal("0.25"), dailyFine);
    }

    @Test
    void shouldCalculateZeroOverdueFineWhenOverdueDaysIsZeroBoundary() {
        // Arrange
        Patron student = new Student("P-1", "Alice", "alice@uni.edu", new LinearFineStrategy());

        // Act
        BigDecimal fine = student.calculateOverdueFine(0L);

        // Assert
        assertEquals(new BigDecimal("0.00"), fine);
    }

    @Test
    void shouldCalculateStudentOverdueFineUsingInjectedLinearStrategy() {
        // Arrange
        Patron student = new Student("P-1", "Alice", "alice@uni.edu", new LinearFineStrategy());

        // Act
        BigDecimal fine = student.calculateOverdueFine(4L);

        // Assert
        assertEquals(new BigDecimal("2.00"), fine);
    }

    @Test
    void shouldCalculateFacultyOverdueFineUsingInjectedLinearStrategy() {
        // Arrange
        Patron faculty = new Faculty("P-2", "Dr. Bob", "bob@uni.edu", new LinearFineStrategy());

        // Act
        BigDecimal fine = faculty.calculateOverdueFine(4L);

        // Assert
        assertEquals(new BigDecimal("1.00"), fine);
    }

    @Test
    void shouldApplyInjectedDecoratedStrategyWhenCalculatingPatronOverdueFine() {
        // Arrange
        FineCalculationStrategy decoratedStrategy = new CappedFineDecorator(
                new GracePeriodFineDecorator(new LinearFineStrategy(), 2L),
                new BigDecimal("3.00")
        );
        Patron faculty = new Faculty("P-2", "Dr. Bob", "bob@uni.edu", decoratedStrategy);

        // Act
        BigDecimal withinGraceFine = faculty.calculateOverdueFine(2L);
        BigDecimal normalFine = faculty.calculateOverdueFine(6L);
        BigDecimal cappedFine = faculty.calculateOverdueFine(30L);

        // Assert
        assertEquals(new BigDecimal("0.00"), withinGraceFine);
        assertEquals(new BigDecimal("1.00"), normalFine);
        assertEquals(new BigDecimal("3.00"), cappedFine);
    }

    @Test
    void shouldThrowInvalidLibraryArgumentExceptionWhenCalculatingOverdueFineWithNegativeDays() {
        // Arrange
        Patron student = new Student("P-1", "Alice", "alice@uni.edu", new LinearFineStrategy());

        // Act & Assert
        assertThrows(
                InvalidLibraryArgumentException.class,
                () -> student.calculateOverdueFine(-1L)
        );
    }

    @Test
    void shouldDelegateFineAssessmentAndExactPaymentToOwnedFineAccount() {
        // Arrange
        Patron student = new Student("P-1", "Alice", "alice@uni.edu", new LinearFineStrategy());
        student.assessFine(new BigDecimal("1.50"));

        // Act
        FinePayment payment = student.payFine("PAY-1", new BigDecimal("1.50"), PAYMENT_DATE);

        // Assert
        assertEquals(new BigDecimal("0.00"), student.getFineBalance());
        assertEquals(new BigDecimal("1.50"), student.getTotalFinesPaid());
        assertEquals(List.of(payment), student.getPaymentHistory());
    }

    @Test
    void shouldThrowInvalidFinePaymentExceptionWhenPatronOverpaysFineByOneCent() {
        // Arrange
        Patron faculty = new Faculty("P-2", "Dr. Bob", "bob@uni.edu", new LinearFineStrategy());
        faculty.assessFine(new BigDecimal("1.00"));

        // Act & Assert
        assertThrows(
                InvalidFinePaymentException.class,
                () -> faculty.payFine("PAY-1", new BigDecimal("1.01"), PAYMENT_DATE)
        );
    }

    @Test
    void shouldThrowInvalidLibraryArgumentExceptionWhenPatronAssessesNegativeFine() {
        // Arrange
        Patron student = new Student("P-1", "Alice", "alice@uni.edu", new LinearFineStrategy());

        // Act & Assert
        assertThrows(
                InvalidLibraryArgumentException.class,
                () -> student.assessFine(new BigDecimal("-0.50"))
        );
    }

    @Test
    void shouldThrowInvalidLibraryArgumentExceptionWhenPatronIdIsBlank() {
        // Arrange
        FineCalculationStrategy strategy = new LinearFineStrategy();

        // Act & Assert
        assertThrows(
                InvalidLibraryArgumentException.class,
                () -> new Student("  ", "Alice", "alice@uni.edu", strategy)
        );
    }

    @Test
    void shouldThrowInvalidLibraryArgumentExceptionWhenPatronNameIsBlank() {
        // Arrange
        FineCalculationStrategy strategy = new LinearFineStrategy();

        // Act & Assert
        assertThrows(
                InvalidLibraryArgumentException.class,
                () -> new Faculty("P-2", "", "bob@uni.edu", strategy)
        );
    }

    @Test
    void shouldThrowInvalidLibraryArgumentExceptionWhenPatronEmailIsNull() {
        // Arrange
        FineCalculationStrategy strategy = new LinearFineStrategy();

        // Act & Assert
        assertThrows(
                InvalidLibraryArgumentException.class,
                () -> new Student("P-1", "Alice", null, strategy)
        );
    }

    @Test
    void shouldThrowInvalidLibraryArgumentExceptionWhenPatronFineStrategyIsNull() {
        // Arrange
        FineCalculationStrategy nullStrategy = null;

        // Act & Assert
        assertThrows(
                InvalidLibraryArgumentException.class,
                () -> new Faculty("P-2", "Dr. Bob", "bob@uni.edu", nullStrategy)
        );
    }

    @Test
    void shouldConsiderTwoStudentsEqualByValueWhenAllFieldsAndAccountMatch() {
        // Arrange
        Student first = new Student("P-1", "Alice", "alice@uni.edu", new LinearFineStrategy());
        Student second = new Student("P-1", "Alice", "alice@uni.edu", new LinearFineStrategy());

        // Act & Assert
        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());
    }

    @Test
    void shouldConsiderTwoStudentsNotEqualWhenFineBalancesDiffer() {
        // Arrange
        Student first = new Student("P-1", "Alice", "alice@uni.edu", new LinearFineStrategy());
        Student second = new Student("P-1", "Alice", "alice@uni.edu", new LinearFineStrategy());
        second.assessFine(new BigDecimal("0.50"));

        // Act & Assert
        assertNotEquals(first, second);
    }

    @Test
    void shouldConsiderTwoStudentsNotEqualWhenFineStrategiesDiffer() {
        // Arrange
        Student first = new Student("P-1", "Alice", "alice@uni.edu", new LinearFineStrategy());
        Student second = new Student(
                "P-1",
                "Alice",
                "alice@uni.edu",
                new TieredFineStrategy(7L, new BigDecimal("2"))
        );

        // Act & Assert
        assertNotEquals(first, second);
    }

    @Test
    void shouldConsiderStudentAndFacultyNotEqualEvenWithIdenticalIdentityAndStrategy() {
        // Arrange
        Patron student = new Student("P-1", "Alex", "alex@uni.edu", new LinearFineStrategy());
        Patron faculty = new Faculty("P-1", "Alex", "alex@uni.edu", new LinearFineStrategy());

        // Act & Assert
        assertNotEquals(student, faculty);
    }

    @Test
    void shouldIncludeSubtypePolicyAndBalanceInPatronToString() {
        // Arrange
        Patron student = new Student("P-1", "Alice", "alice@uni.edu", new LinearFineStrategy());
        Patron faculty = new Faculty("P-2", "Dr. Bob", "bob@uni.edu", new LinearFineStrategy());

        // Act
        String studentString = student.toString();
        String facultyString = faculty.toString();

        // Assert
        assertTrue(studentString.contains("Student")
                && studentString.contains("borrowingLimit=3")
                && studentString.contains("dailyOverdueFine=0.50"));
        assertTrue(facultyString.contains("Faculty")
                && facultyString.contains("borrowingLimit=10")
                && facultyString.contains("dailyOverdueFine=0.25"));
    }
}
