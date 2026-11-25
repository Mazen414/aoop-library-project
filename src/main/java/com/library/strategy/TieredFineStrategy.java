package com.library.strategy;

import com.library.exception.InvalidLibraryArgumentException;
import com.library.util.Validation;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Tiered fine calculation strategy that charges {@code dailyRate} for up to {@code thresholdDays}
 * and multiplies {@code dailyRate} by {@code escalatedMultiplier} for every day beyond the threshold.
 */
public final class TieredFineStrategy implements FineCalculationStrategy {

    private final long thresholdDays;
    private final BigDecimal escalatedMultiplier;

    public TieredFineStrategy(long thresholdDays, BigDecimal escalatedMultiplier) {
        this.thresholdDays = Validation.requirePositive(thresholdDays, "thresholdDays");
        Validation.requireNonNull(escalatedMultiplier, "escalatedMultiplier");
        if (escalatedMultiplier.compareTo(BigDecimal.ONE) < 0) {
            throw new InvalidLibraryArgumentException(
                    "escalatedMultiplier must be at least 1, but was: " + escalatedMultiplier
            );
        }
        this.escalatedMultiplier = escalatedMultiplier.stripTrailingZeros();
    }

    public long getThresholdDays() {
        return thresholdDays;
    }

    public BigDecimal getEscalatedMultiplier() {
        return escalatedMultiplier;
    }

    @Override
    public BigDecimal calculateFine(long overdueDays, BigDecimal dailyRate) {
        Validation.requireNonNegative(overdueDays, "overdueDays");
        BigDecimal normalizedRate = Validation.requireNonNegativeMoney(dailyRate, "dailyRate");
        if (overdueDays == 0L) {
            return Validation.ZERO_MONEY;
        }
        long standardDays = Math.min(overdueDays, thresholdDays);
        long escalatedDays = Math.max(0L, overdueDays - thresholdDays);

        BigDecimal standardFine = normalizedRate.multiply(BigDecimal.valueOf(standardDays));
        BigDecimal escalatedFine = normalizedRate
                .multiply(escalatedMultiplier)
                .multiply(BigDecimal.valueOf(escalatedDays));

        return Validation.normalizeMoney(standardFine.add(escalatedFine), "fine");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        TieredFineStrategy that = (TieredFineStrategy) o;
        return thresholdDays == that.thresholdDays
                && Objects.equals(escalatedMultiplier, that.escalatedMultiplier);
    }

    @Override
    public int hashCode() {
        return Objects.hash(thresholdDays, escalatedMultiplier);
    }

    @Override
    public String toString() {
        return "TieredFineStrategy{thresholdDays=" + thresholdDays
                + ", escalatedMultiplier=" + escalatedMultiplier.toPlainString()
                + "}";
    }
}
