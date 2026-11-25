package com.library.strategy;

import com.library.util.Validation;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Decorator that waives the first {@code graceDays} overdue days before delegating
 * the remaining chargeable days to the wrapped {@link FineCalculationStrategy}.
 */
public final class GracePeriodFineDecorator extends FineStrategyDecorator {

    private final long graceDays;

    public GracePeriodFineDecorator(FineCalculationStrategy wrapped, long graceDays) {
        super(wrapped);
        this.graceDays = Validation.requireNonNegative(graceDays, "graceDays");
    }

    public long getGraceDays() {
        return graceDays;
    }

    @Override
    public BigDecimal calculateFine(long overdueDays, BigDecimal dailyRate) {
        Validation.requireNonNegative(overdueDays, "overdueDays");
        BigDecimal normalizedRate = Validation.requireNonNegativeMoney(dailyRate, "dailyRate");
        long chargeableDays = Math.max(0L, overdueDays - graceDays);
        return Validation.normalizeMoney(wrapped.calculateFine(chargeableDays, normalizedRate), "fine");
    }

    @Override
    public boolean equals(Object o) {
        if (!super.equals(o)) {
            return false;
        }
        GracePeriodFineDecorator that = (GracePeriodFineDecorator) o;
        return graceDays == that.graceDays;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), graceDays);
    }

    @Override
    public String toString() {
        return "GracePeriodFineDecorator{graceDays=" + graceDays
                + ", wrapped=" + wrapped
                + "}";
    }
}
