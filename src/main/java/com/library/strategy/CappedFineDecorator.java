package com.library.strategy;

import com.library.util.Validation;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Decorator that enforces an upper cap ({@code maxFine}) on the fine produced by
 * the wrapped {@link FineCalculationStrategy}.
 */
public final class CappedFineDecorator extends FineStrategyDecorator {

    private final BigDecimal maxFine;

    public CappedFineDecorator(FineCalculationStrategy wrapped, BigDecimal maxFine) {
        super(wrapped);
        this.maxFine = Validation.requireNonNegativeMoney(maxFine, "maxFine");
    }

    public BigDecimal getMaxFine() {
        return maxFine;
    }

    @Override
    public BigDecimal calculateFine(long overdueDays, BigDecimal dailyRate) {
        Validation.requireNonNegative(overdueDays, "overdueDays");
        BigDecimal normalizedRate = Validation.requireNonNegativeMoney(dailyRate, "dailyRate");
        BigDecimal calculatedFine = Validation.normalizeMoney(
                wrapped.calculateFine(overdueDays, normalizedRate),
                "fine"
        );
        return calculatedFine.min(maxFine);
    }

    @Override
    public boolean equals(Object o) {
        if (!super.equals(o)) {
            return false;
        }
        CappedFineDecorator that = (CappedFineDecorator) o;
        return Objects.equals(maxFine, that.maxFine);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), maxFine);
    }

    @Override
    public String toString() {
        return "CappedFineDecorator{maxFine=" + maxFine
                + ", wrapped=" + wrapped
                + "}";
    }
}
