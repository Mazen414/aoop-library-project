package com.library.util;

import com.library.exception.InvalidLibraryArgumentException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ValidationTest {

    @Test
    void shouldReturnOriginalObjectWhenRequireNonNullReceivesValidObject() {
        // Arrange
        Object expected = new Object();

        // Act
        Object actual = Validation.requireNonNull(expected, "item");

        // Assert
        assertSame(expected, actual);
    }

    @Test
    void shouldThrowInvalidLibraryArgumentExceptionWhenRequireNonNullReceivesNull() {
        // Arrange
        Object input = null;

        // Act & Assert
        InvalidLibraryArgumentException exception = assertThrows(
                InvalidLibraryArgumentException.class,
                () -> Validation.requireNonNull(input, "borrowDate")
        );
        assertTrue(exception.getMessage().contains("borrowDate"));
    }

    @Test
    void shouldReturnTrimmedStringWhenRequireNonBlankReceivesValidText() {
        // Arrange
        String input = "  Clean Code  ";

        // Act
        String result = Validation.requireNonBlank(input, "title");

        // Assert
        assertEquals("Clean Code", result);
    }

    @Test
    void shouldThrowInvalidLibraryArgumentExceptionWhenRequireNonBlankReceivesNull() {
        // Arrange
        String input = null;

        // Act & Assert
        assertThrows(
                InvalidLibraryArgumentException.class,
                () -> Validation.requireNonBlank(input, "title")
        );
    }

    @Test
    void shouldThrowInvalidLibraryArgumentExceptionWhenRequireNonBlankReceivesEmptyString() {
        // Arrange
        String input = "";

        // Act & Assert
        assertThrows(
                InvalidLibraryArgumentException.class,
                () -> Validation.requireNonBlank(input, "title")
        );
    }

    @Test
    void shouldThrowInvalidLibraryArgumentExceptionWhenRequireNonBlankReceivesWhitespaceOnly() {
        // Arrange
        String input = "   \t\n  ";

        // Act & Assert
        assertThrows(
                InvalidLibraryArgumentException.class,
                () -> Validation.requireNonBlank(input, "title")
        );
    }

    @Test
    void shouldReturnValueWhenRequirePositiveIntReceivesSmallestPositiveBoundary() {
        // Arrange
        int input = 1;

        // Act
        int result = Validation.requirePositive(input, "durationMinutes");

        // Assert
        assertEquals(1, result);
    }

    @Test
    void shouldThrowInvalidLibraryArgumentExceptionWhenRequirePositiveIntReceivesZero() {
        // Arrange
        int input = 0;

        // Act & Assert
        assertThrows(
                InvalidLibraryArgumentException.class,
                () -> Validation.requirePositive(input, "durationMinutes")
        );
    }

    @Test
    void shouldThrowInvalidLibraryArgumentExceptionWhenRequirePositiveIntReceivesNegativeValue() {
        // Arrange
        int input = -5;

        // Act & Assert
        assertThrows(
                InvalidLibraryArgumentException.class,
                () -> Validation.requirePositive(input, "durationMinutes")
        );
    }

    @Test
    void shouldReturnValueWhenRequirePositiveLongReceivesSmallestPositiveBoundary() {
        // Arrange
        long input = 1L;

        // Act
        long result = Validation.requirePositive(input, "thresholdDays");

        // Assert
        assertEquals(1L, result);
    }

    @Test
    void shouldThrowInvalidLibraryArgumentExceptionWhenRequirePositiveLongReceivesZero() {
        // Arrange
        long input = 0L;

        // Act & Assert
        assertThrows(
                InvalidLibraryArgumentException.class,
                () -> Validation.requirePositive(input, "thresholdDays")
        );
    }

    @Test
    void shouldThrowInvalidLibraryArgumentExceptionWhenRequirePositiveLongReceivesNegativeValue() {
        // Arrange
        long input = -1L;

        // Act & Assert
        assertThrows(
                InvalidLibraryArgumentException.class,
                () -> Validation.requirePositive(input, "thresholdDays")
        );
    }

    @Test
    void shouldReturnZeroWhenRequireNonNegativeLongReceivesZeroBoundary() {
        // Arrange
        long input = 0L;

        // Act
        long result = Validation.requireNonNegative(input, "overdueDays");

        // Assert
        assertEquals(0L, result);
    }

    @Test
    void shouldReturnValueWhenRequireNonNegativeLongReceivesPositiveValue() {
        // Arrange
        long input = 14L;

        // Act
        long result = Validation.requireNonNegative(input, "overdueDays");

        // Assert
        assertEquals(14L, result);
    }

    @Test
    void shouldThrowInvalidLibraryArgumentExceptionWhenRequireNonNegativeLongReceivesNegativeBoundary() {
        // Arrange
        long input = -1L;

        // Act & Assert
        assertThrows(
                InvalidLibraryArgumentException.class,
                () -> Validation.requireNonNegative(input, "overdueDays")
        );
    }

    @Test
    void shouldScaleMoneyToTwoDecimalPlacesWhenNormalizeMoneyReceivesOneDecimalPlace() {
        // Arrange
        BigDecimal input = new BigDecimal("1.5");

        // Act
        BigDecimal result = Validation.normalizeMoney(input, "amount");

        // Assert
        assertEquals(new BigDecimal("1.50"), result);
    }

    @Test
    void shouldRoundHalfUpToTwoDecimalPlacesWhenNormalizeMoneyReceivesThirdDecimalFive() {
        // Arrange
        BigDecimal input = new BigDecimal("0.335");

        // Act
        BigDecimal result = Validation.normalizeMoney(input, "amount");

        // Assert
        assertEquals(new BigDecimal("0.34"), result);
    }

    @Test
    void shouldThrowInvalidLibraryArgumentExceptionWhenNormalizeMoneyReceivesNull() {
        // Arrange
        BigDecimal input = null;

        // Act & Assert
        assertThrows(
                InvalidLibraryArgumentException.class,
                () -> Validation.normalizeMoney(input, "amount")
        );
    }

    @Test
    void shouldReturnCanonicalZeroWhenRequireNonNegativeMoneyReceivesZeroBoundary() {
        // Arrange
        BigDecimal input = BigDecimal.ZERO;

        // Act
        BigDecimal result = Validation.requireNonNegativeMoney(input, "fine");

        // Assert
        assertEquals(Validation.ZERO_MONEY, result);
    }

    @Test
    void shouldReturnNormalizedAmountWhenRequireNonNegativeMoneyReceivesPositiveAmount() {
        // Arrange
        BigDecimal input = new BigDecimal("2.5");

        // Act
        BigDecimal result = Validation.requireNonNegativeMoney(input, "fine");

        // Assert
        assertEquals(new BigDecimal("2.50"), result);
    }

    @Test
    void shouldThrowInvalidLibraryArgumentExceptionWhenRequireNonNegativeMoneyReceivesNull() {
        // Arrange
        BigDecimal input = null;

        // Act & Assert
        assertThrows(
                InvalidLibraryArgumentException.class,
                () -> Validation.requireNonNegativeMoney(input, "fine")
        );
    }

    @Test
    void shouldThrowInvalidLibraryArgumentExceptionWhenRequireNonNegativeMoneyReceivesNegativeCent() {
        // Arrange
        BigDecimal input = new BigDecimal("-0.01");

        // Act & Assert
        assertThrows(
                InvalidLibraryArgumentException.class,
                () -> Validation.requireNonNegativeMoney(input, "fine")
        );
    }

    @Test
    void shouldThrowInvalidLibraryArgumentExceptionWhenRequireNonNegativeMoneyReceivesSubCentNegativeValue() {
        // Arrange
        BigDecimal input = new BigDecimal("-0.001");

        // Act & Assert
        assertThrows(
                InvalidLibraryArgumentException.class,
                () -> Validation.requireNonNegativeMoney(input, "fine")
        );
    }
}
