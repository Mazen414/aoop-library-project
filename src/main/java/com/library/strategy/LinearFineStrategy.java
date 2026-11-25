package com.library.strategy;

import com.library.util.Validation;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Standard linear fine calculation strategy: {@code overdueDays * dailyRate}.
 */
public final class LinearFineStrategy implements FineCalculationStrategy {

    @Override
    public BigDecimal calculateFine(long overdueDays, BigDecimal dailyRate) {
        Validation.requireNonNegative(overdueDays, "overdueDays");
        BigDecimal normalizedRate = Validation.requireNonNegativeMoney(dailyRate, "dailyRate");
        if (overdueDays == 0L) {
            return Validation.ZERO_MONEY;
        }
        BigDecimal rawFine = normalizedRate.multiply(BigDecimal.valueOf(overdueDays));
        return Validation.normalizeMoney(rawFine, "fine");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        return o != null && getClass() == o.getClass();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getClass());
    }

    @Override
    public String toString() {
        return "LinearFineStrategy{}";
    }
}
