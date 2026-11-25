package com.library.strategy;

import com.library.exception.InvalidLibraryArgumentException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FineCalculationStrategyTest {

    @Test
    void shouldReturnZeroFineWhenLinearStrategyReceivesZeroOverdueDays() {
        // Arrange
        FineCalculationStrategy strategy = new LinearFineStrategy();
        BigDecimal dailyRate = new BigDecimal("0.50");

        // Act
        BigDecimal fine = strategy.calculateFine(0L, dailyRate);

        // Assert
        assertEquals(new BigDecimal("0.00"), fine);
    }

    @Test
    void shouldChargeSingleDayRateWhenLinearStrategyReceivesOneOverdueDay() {
        // Arrange
        FineCalculationStrategy strategy = new LinearFineStrategy();
        BigDecimal dailyRate = new BigDecimal("0.50");

        // Act
        BigDecimal fine = strategy.calculateFine(1L, dailyRate);

        // Assert
        assertEquals(new BigDecimal("0.50"), fine);
    }

    @Test
    void shouldNormalizeLinearFineToTwoDecimalPlacesWhenRateHasOneDecimalPlace() {
        // Arrange
        FineCalculationStrategy strategy = new LinearFineStrategy();
        BigDecimal dailyRate = new BigDecimal("0.5");

        // Act
        BigDecimal fine = strategy.calculateFine(5L, dailyRate);

        // Assert
        assertEquals(new BigDecimal("2.50"), fine);
    }

    @Test
    void shouldReturnZeroFineWhenLinearStrategyReceivesZeroDailyRateBoundary() {
        // Arrange
        FineCalculationStrategy strategy = new LinearFineStrategy();

        // Act
        BigDecimal fine = strategy.calculateFine(10L, BigDecimal.ZERO);

        // Assert
        assertEquals(new BigDecimal("0.00"), fine);
    }

    @Test
    void shouldThrowInvalidLibraryArgumentExceptionWhenLinearStrategyReceivesNegativeOverdueDays() {
        // Arrange
        FineCalculationStrategy strategy = new LinearFineStrategy();
        BigDecimal dailyRate = new BigDecimal("0.50");

        // Act & Assert
        assertThrows(
                InvalidLibraryArgumentException.class,
                () -> strategy.calculateFine(-1L, dailyRate)
        );
    }

    @Test
    void shouldThrowInvalidLibraryArgumentExceptionWhenLinearStrategyReceivesNullDailyRate() {
        // Arrange
        FineCalculationStrategy strategy = new LinearFineStrategy();

        // Act & Assert
        assertThrows(
                InvalidLibraryArgumentException.class,
                () -> strategy.calculateFine(3L, null)
        );
    }

    @Test
    void shouldThrowInvalidLibraryArgumentExceptionWhenLinearStrategyReceivesNegativeDailyRate() {
        // Arrange
        FineCalculationStrategy strategy = new LinearFineStrategy();
        BigDecimal negativeRate = new BigDecimal("-0.01");

        // Act & Assert
        assertThrows(
                InvalidLibraryArgumentException.class,
                () -> strategy.calculateFine(3L, negativeRate)
        );
    }

    @Test
    void shouldConsiderTwoLinearFineStrategyInstancesEqualByValue() {
        // Arrange
        LinearFineStrategy first = new LinearFineStrategy();
        LinearFineStrategy second = new LinearFineStrategy();

        // Act & Assert
        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());
        assertEquals("LinearFineStrategy{}", first.toString());
    }

    @Test
    void shouldReturnZeroFineWhenTieredStrategyReceivesZeroOverdueDays() {
        // Arrange
        FineCalculationStrategy strategy = new TieredFineStrategy(7L, new BigDecimal("2"));
        BigDecimal dailyRate = new BigDecimal("0.50");

        // Act
        BigDecimal fine = strategy.calculateFine(0L, dailyRate);

        // Assert
        assertEquals(new BigDecimal("0.00"), fine);
    }

    @Test
    void shouldChargeSingleDayRateWhenTieredStrategyReceivesOneOverdueDay() {
        // Arrange
        FineCalculationStrategy strategy = new TieredFineStrategy(7L, new BigDecimal("2"));
        BigDecimal dailyRate = new BigDecimal("0.25");

        // Act
        BigDecimal fine = strategy.calculateFine(1L, dailyRate);

        // Assert
        assertEquals(new BigDecimal("0.25"), fine);
    }

    @Test
    void shouldChargeOnlyStandardRateWhenOverdueDaysEqualsExactTierThreshold() {
        // Arrange
        FineCalculationStrategy strategy = new TieredFineStrategy(7L, new BigDecimal("2"));
        BigDecimal dailyRate = new BigDecimal("0.50");

        // Act
        BigDecimal fine = strategy.calculateFine(7L, dailyRate);

        // Assert
        assertEquals(new BigDecimal("3.50"), fine);
    }

    @Test
    void shouldApplyEscalatedMultiplierWhenOverdueDaysIsOneDayBeyondThreshold() {
        // Arrange
        FineCalculationStrategy strategy = new TieredFineStrategy(7L, new BigDecimal("2"));
        BigDecimal dailyRate = new BigDecimal("0.50");

        // Act
        BigDecimal fine = strategy.calculateFine(8L, dailyRate);

        // Assert
        assertEquals(new BigDecimal("4.50"), fine);
    }

    @Test
    void shouldRoundHalfUpToTwoDecimalPlacesWhenTieredCalculationProducesHalfCent() {
        // Arrange
        FineCalculationStrategy strategy = new TieredFineStrategy(3L, new BigDecimal("1.5"));
        BigDecimal dailyRate = new BigDecimal("0.25");

        // Act
        BigDecimal fine = strategy.calculateFine(4L, dailyRate);

        // Assert
        assertEquals(new BigDecimal("1.13"), fine);
    }

    @Test
    void shouldAcceptMinimumMultiplierBoundaryOfOneWhenCreatingTieredStrategy() {
        // Arrange
        FineCalculationStrategy strategy = new TieredFineStrategy(5L, BigDecimal.ONE);
        BigDecimal dailyRate = new BigDecimal("0.50");

        // Act
        BigDecimal fine = strategy.calculateFine(6L, dailyRate);

        // Assert
        assertEquals(new BigDecimal("3.00"), fine);
    }

    @Test
    void shouldThrowInvalidLibraryArgumentExceptionWhenTieredThresholdIsZeroBoundary() {
        // Arrange
        long zeroThreshold = 0L;

        // Act & Assert
        assertThrows(
                InvalidLibraryArgumentException.class,
                () -> new TieredFineStrategy(zeroThreshold, new BigDecimal("2"))
        );
    }

    @Test
    void shouldThrowInvalidLibraryArgumentExceptionWhenTieredThresholdIsNegative() {
        // Arrange
        long negativeThreshold = -3L;

        // Act & Assert
        assertThrows(
                InvalidLibraryArgumentException.class,
                () -> new TieredFineStrategy(negativeThreshold, new BigDecimal("2"))
        );
    }

    @Test
    void shouldThrowInvalidLibraryArgumentExceptionWhenTieredMultiplierIsNull() {
        // Arrange
        BigDecimal nullMultiplier = null;

        // Act & Assert
        assertThrows(
                InvalidLibraryArgumentException.class,
                () -> new TieredFineStrategy(7L, nullMultiplier)
        );
    }

    @Test
    void shouldThrowInvalidLibraryArgumentExceptionWhenTieredMultiplierIsBelowOneBoundary() {
        // Arrange
        BigDecimal invalidMultiplier = new BigDecimal("0.99");

        // Act & Assert
        assertThrows(
                InvalidLibraryArgumentException.class,
                () -> new TieredFineStrategy(7L, invalidMultiplier)
        );
    }

    @Test
    void shouldThrowInvalidLibraryArgumentExceptionWhenTieredStrategyReceivesNegativeOverdueDays() {
        // Arrange
        FineCalculationStrategy strategy = new TieredFineStrategy(7L, new BigDecimal("2"));

        // Act & Assert
        assertThrows(
                InvalidLibraryArgumentException.class,
                () -> strategy.calculateFine(-1L, new BigDecimal("0.50"))
        );
    }

    @Test
    void shouldThrowInvalidLibraryArgumentExceptionWhenTieredStrategyReceivesNegativeDailyRate() {
        // Arrange
        FineCalculationStrategy strategy = new TieredFineStrategy(7L, new BigDecimal("2"));

        // Act & Assert
        assertThrows(
                InvalidLibraryArgumentException.class,
                () -> strategy.calculateFine(5L, new BigDecimal("-0.25"))
        );
    }

    @Test
    void shouldConsiderTwoTieredStrategiesEqualByValueEvenWhenMultiplierScalesDiffer() {
        // Arrange
        TieredFineStrategy first = new TieredFineStrategy(7L, new BigDecimal("2"));
        TieredFineStrategy second = new TieredFineStrategy(7L, new BigDecimal("2.00"));

        // Act & Assert
        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());
    }

    @Test
    void shouldConsiderTieredStrategyNotEqualToLinearStrategy() {
        // Arrange
        FineCalculationStrategy tiered = new TieredFineStrategy(7L, new BigDecimal("2"));
        FineCalculationStrategy linear = new LinearFineStrategy();

        // Act & Assert
        assertNotEquals(tiered, linear);
    }

    @Test
    void shouldIncludeThresholdAndMultiplierInTieredStrategyToString() {
        // Arrange
        TieredFineStrategy strategy = new TieredFineStrategy(7L, new BigDecimal("1.50"));

        // Act
        String description = strategy.toString();

        // Assert
        assertTrue(description.contains("7") && description.contains("1.5"));
    }
}
