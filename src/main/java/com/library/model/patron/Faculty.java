package com.library.model.patron;

import com.library.strategy.FineCalculationStrategy;

import java.math.BigDecimal;

/**
 * Represents a faculty patron with a borrowing limit of 10 active loans
 * and a daily overdue fine rate of $0.25.
 */
public final class Faculty extends Patron {

    public static final int BORROWING_LIMIT = 10;
    public static final BigDecimal DAILY_OVERDUE_FINE = new BigDecimal("0.25");

    public Faculty(String id, String name, String email, FineCalculationStrategy fineStrategy) {
        super(id, name, email, fineStrategy);
    }

    @Override
    public int getBorrowingLimit() {
        return BORROWING_LIMIT;
    }

    @Override
    public BigDecimal getDailyOverdueFine() {
        return DAILY_OVERDUE_FINE;
    }
}
