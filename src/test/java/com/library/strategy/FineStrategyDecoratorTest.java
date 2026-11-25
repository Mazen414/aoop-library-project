package com.library.strategy;

import com.library.exception.InvalidLibraryArgumentException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FineStrategyDecoratorTest {

    @Test
    void shouldReturnZeroFineWhenOverdueDaysEqualsExactGracePeriodBoundary() {
        // Arrange
        FineCalculationStrategy strategy = new GracePeriodFineDecorator(new LinearFineStrategy(), 2L);
        BigDecimal dailyRate = new BigDecimal("0.50");

        // Act
        BigDecimal fine = strategy.calculateFine(2L, dailyRate);

        // Assert
        assertEquals(new BigDecimal("0.00"), fine);
    }

    @Test
    void shouldChargeOneDayFineWhenOverdueDaysIsOneDayPastGracePeriod() {
        // Arrange
        FineCalculationStrategy strategy = new GracePeriodFineDecorator(new LinearFineStrategy(), 2L);
        BigDecimal dailyRate = new BigDecimal("0.50");

        // Act
        BigDecimal fine = strategy.calculateFine(3L, dailyRate);

        // Assert
        assertEquals(new BigDecimal("0.50"), fine);
    }

    @Test
    void shouldReturnZeroFineWhenGracePeriodDecoratorReceivesZeroOverdueDays() {
        // Arrange
        FineCalculationStrategy strategy = new GracePeriodFineDecorator(new LinearFineStrategy(), 2L);

        // Act
        BigDecimal fine = strategy.calculateFine(0L, new BigDecimal("0.25"));

        // Assert
        assertEquals(new BigDecimal("0.00"), fine);
    }

    @Test
    void shouldChargeAllOverdueDaysWhenGracePeriodIsZeroBoundary() {
        // Arrange
        FineCalculationStrategy strategy = new GracePeriodFineDecorator(new LinearFineStrategy(), 0L);

        // Act
        BigDecimal fine = strategy.calculateFine(3L, new BigDecimal("0.50"));

        // Assert
        assertEquals(new BigDecimal("1.50"), fine);
    }

    @Test
    void shouldThrowInvalidLibraryArgumentExceptionWhenGracePeriodWrapsNullStrategy() {
        // Arrange
        FineCalculationStrategy nullWrapped = null;

        // Act & Assert
        assertThrows(
                InvalidLibraryArgumentException.class,
                () -> new GracePeriodFineDecorator(nullWrapped, 2L)
        );
    }

    @Test
    void shouldThrowInvalidLibraryArgumentExceptionWhenGraceDaysIsNegativeBoundary() {
        // Arrange
        FineCalculationStrategy base = new LinearFineStrategy();

        // Act & Assert
        assertThrows(
                InvalidLibraryArgumentException.class,
                () -> new GracePeriodFineDecorator(base, -1L)
        );
    }

    @Test
    void shouldThrowInvalidLibraryArgumentExceptionWhenGracePeriodReceivesNegativeOverdueDays() {
        // Arrange
        FineCalculationStrategy strategy = new GracePeriodFineDecorator(new LinearFineStrategy(), 2L);

        // Act & Assert
        assertThrows(
                InvalidLibraryArgumentException.class,
                () -> strategy.calculateFine(-1L, new BigDecimal("0.50"))
        );
    }

    @Test
    void shouldThrowInvalidLibraryArgumentExceptionWhenGracePeriodReceivesNullDailyRateEvenWithinGraceDays() {
        // Arrange
        FineCalculationStrategy strategy = new GracePeriodFineDecorator(new LinearFineStrategy(), 5L);

        // Act & Assert
        assertThrows(
                InvalidLibraryArgumentException.class,
                () -> strategy.calculateFine(2L, null)
        );
    }

    @Test
    void shouldReturnExactFineWhenCalculatedAmountIsBelowCap() {
        // Arrange
        FineCalculationStrategy strategy = new CappedFineDecorator(new LinearFineStrategy(), new BigDecimal("5.00"));

        // Act
        BigDecimal fine = strategy.calculateFine(4L, new BigDecimal("0.50"));

        // Assert
        assertEquals(new BigDecimal("2.00"), fine);
    }

    @Test
    void shouldReturnExactCapWhenCalculatedFineEqualsCapBoundary() {
        // Arrange
        FineCalculationStrategy strategy = new CappedFineDecorator(new LinearFineStrategy(), new BigDecimal("5.00"));

        // Act
        BigDecimal fine = strategy.calculateFine(500L, new BigDecimal("0.01"));

        // Assert
        assertEquals(new BigDecimal("5.00"), fine);
    }

    @Test
    void shouldClampFineToMaxCapWhenCalculatedFineExceedsCapByOneCent() {
        // Arrange
        FineCalculationStrategy strategy = new CappedFineDecorator(new LinearFineStrategy(), new BigDecimal("5.00"));

        // Act
        BigDecimal fine = strategy.calculateFine(501L, new BigDecimal("0.01"));

        // Assert
        assertEquals(new BigDecimal("5.00"), fine);
    }

    @Test
    void shouldReturnZeroFineWhenMaxFineCapIsZeroBoundary() {
        // Arrange
        FineCalculationStrategy strategy = new CappedFineDecorator(new LinearFineStrategy(), BigDecimal.ZERO);

        // Act
        BigDecimal fine = strategy.calculateFine(10L, new BigDecimal("0.50"));

        // Assert
        assertEquals(new BigDecimal("0.00"), fine);
    }

    @Test
    void shouldThrowInvalidLibraryArgumentExceptionWhenCappedDecoratorWrapsNullStrategy() {
        // Arrange
        FineCalculationStrategy nullWrapped = null;

        // Act & Assert
        assertThrows(
                InvalidLibraryArgumentException.class,
                () -> new CappedFineDecorator(nullWrapped, new BigDecimal("5.00"))
        );
    }

    @Test
    void shouldThrowInvalidLibraryArgumentExceptionWhenMaxFineCapIsNull() {
        // Arrange
        FineCalculationStrategy base = new LinearFineStrategy();

        // Act & Assert
        assertThrows(
                InvalidLibraryArgumentException.class,
                () -> new CappedFineDecorator(base, null)
        );
    }

    @Test
    void shouldThrowInvalidLibraryArgumentExceptionWhenMaxFineCapIsNegativeBoundary() {
        // Arrange
        FineCalculationStrategy base = new LinearFineStrategy();

        // Act & Assert
        assertThrows(
                InvalidLibraryArgumentException.class,
                () -> new CappedFineDecorator(base, new BigDecimal("-0.01"))
        );
    }

    @Test
    void shouldThrowInvalidLibraryArgumentExceptionWhenCappedDecoratorReceivesNegativeOverdueDays() {
        // Arrange
        FineCalculationStrategy strategy = new CappedFineDecorator(new LinearFineStrategy(), new BigDecimal("5.00"));

        // Act & Assert
        assertThrows(
                InvalidLibraryArgumentException.class,
                () -> strategy.calculateFine(-1L, new BigDecimal("0.50"))
        );
    }

    @Test
    void shouldThrowInvalidLibraryArgumentExceptionWhenCappedDecoratorReceivesNegativeDailyRate() {
        // Arrange
        FineCalculationStrategy strategy = new CappedFineDecorator(new LinearFineStrategy(), new BigDecimal("5.00"));

        // Act & Assert
        assertThrows(
                InvalidLibraryArgumentException.class,
                () -> strategy.calculateFine(5L, new BigDecimal("-0.50"))
        );
    }

    @Test
    void shouldApplyGracePeriodBeforeCappingWhenDecoratorsAreStackedAroundLinearStrategy() {
        // Arrange
        FineCalculationStrategy strategy = new CappedFineDecorator(
                new GracePeriodFineDecorator(new LinearFineStrategy(), 2L),
                new BigDecimal("5.00")
        );

        // Act
        BigDecimal fine = strategy.calculateFine(5L, new BigDecimal("0.50"));

        // Assert
        assertEquals(new BigDecimal("1.50"), fine);
    }

    @Test
    void shouldEnforceCapAfterGracePeriodAndTierEscalationWhenDecoratorsAreStackedAroundTieredStrategy() {
        // Arrange
        FineCalculationStrategy strategy = new CappedFineDecorator(
                new GracePeriodFineDecorator(new TieredFineStrategy(5L, new BigDecimal("2")), 2L),
                new BigDecimal("10.00")
        );

        // Act
        BigDecimal fine = strategy.calculateFine(20L, new BigDecimal("0.50"));

        // Assert
        assertEquals(new BigDecimal("10.00"), fine);
    }

    @Test
    void shouldCalculateUncappedTieredFineAfterGracePeriodWhenTotalIsBelowCap() {
        // Arrange
        FineCalculationStrategy strategy = new CappedFineDecorator(
                new GracePeriodFineDecorator(new TieredFineStrategy(5L, new BigDecimal("2")), 2L),
                new BigDecimal("10.00")
        );

        // Act
        BigDecimal fine = strategy.calculateFine(8L, new BigDecimal("0.50"));

        // Assert
        assertEquals(new BigDecimal("3.50"), fine);
    }

    @Test
    void shouldConsiderTwoGracePeriodDecoratorsEqualByValueWhenFieldsMatch() {
        // Arrange
        GracePeriodFineDecorator first = new GracePeriodFineDecorator(new LinearFineStrategy(), 2L);
        GracePeriodFineDecorator second = new GracePeriodFineDecorator(new LinearFineStrategy(), 2L);

        // Act & Assert
        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());
    }

    @Test
    void shouldConsiderTwoCappedDecoratorsEqualByValueEvenWhenCapInputScalesDiffer() {
        // Arrange
        CappedFineDecorator first = new CappedFineDecorator(new LinearFineStrategy(), new BigDecimal("10"));
        CappedFineDecorator second = new CappedFineDecorator(new LinearFineStrategy(), new BigDecimal("10.00"));

        // Act & Assert
        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());
    }

    @Test
    void shouldConsiderDifferentDecoratorTypesNotEqualEvenWhenWrappingSameStrategy() {
        // Arrange
        FineCalculationStrategy grace = new GracePeriodFineDecorator(new LinearFineStrategy(), 2L);
        FineCalculationStrategy capped = new CappedFineDecorator(new LinearFineStrategy(), new BigDecimal("2.00"));

        // Act & Assert
        assertNotEquals(grace, capped);
    }

    @Test
    void shouldIncludeConfigurationAndWrappedStrategyInDecoratorToString() {
        // Arrange
        FineCalculationStrategy stacked = new CappedFineDecorator(
                new GracePeriodFineDecorator(new LinearFineStrategy(), 2L),
                new BigDecimal("15.00")
        );

        // Act
        String description = stacked.toString();

        // Assert
        assertTrue(description.contains("15.00")
                && description.contains("GracePeriodFineDecorator")
                && description.contains("LinearFineStrategy"));
    }
}
