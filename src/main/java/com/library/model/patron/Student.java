package com.library.model.patron;

import com.library.strategy.FineCalculationStrategy;

import java.math.BigDecimal;

/**
 * Represents a student patron with a borrowing limit of 3 active loans
 * and a daily overdue fine rate of $0.50.
 */
public final class Student extends Patron {

    public static final int BORROWING_LIMIT = 3;
    public static final BigDecimal DAILY_OVERDUE_FINE = new BigDecimal("0.50");

    public Student(String id, String name, String email, FineCalculationStrategy fineStrategy) {
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
