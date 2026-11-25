package com.library.strategy;

import java.math.BigDecimal;

/**
 * Strategy interface for calculating overdue fines based on the number of overdue days
 * and a patron's daily overdue rate.
 *
 * <p>Implementations must validate that {@code overdueDays} is non-negative and {@code dailyRate}
 * is non-null and non-negative, return {@code 0.00} when {@code overdueDays} is zero, and return
 * a non-negative monetary amount normalized to scale 2 ({@link java.math.RoundingMode#HALF_UP}).
 */
@FunctionalInterface
public interface FineCalculationStrategy {

    /**
     * Calculates the overdue fine for the given number of overdue days and daily rate.
     *
     * @param overdueDays non-negative number of days past the due date
     * @param dailyRate   non-negative daily overdue fine rate
     * @return non-negative overdue fine normalized to 2 decimal places
     */
    BigDecimal calculateFine(long overdueDays, BigDecimal dailyRate);
}
