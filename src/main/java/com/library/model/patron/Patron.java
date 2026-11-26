package com.library.model.patron;

import com.library.strategy.FineCalculationStrategy;
import com.library.util.Validation;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

/**
 * Abstract base class representing a registered library patron.
 * Subclasses define polymorphic borrowing limits and daily overdue fine rates,
 * while delegating overdue calculation to an injected {@link FineCalculationStrategy}
 * and fine ledger management to a composed {@link FineAccount}.
 */
public abstract class Patron {

    private final String id;
    private final String name;
    private final String email;
    private final FineCalculationStrategy fineStrategy;
    private final FineAccount fineAccount;

    protected Patron(String id, String name, String email, FineCalculationStrategy fineStrategy) {
        this.id = Validation.requireNonBlank(id, "id");
        this.name = Validation.requireNonBlank(name, "name");
        this.email = Validation.requireNonBlank(email, "email");
        this.fineStrategy = Validation.requireNonNull(fineStrategy, "fineStrategy");
        this.fineAccount = new FineAccount();
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public FineCalculationStrategy getFineStrategy() {
        return fineStrategy;
    }

    /**
     * Returns the maximum number of simultaneous active loans allowed for this patron type.
     */
    public abstract int getBorrowingLimit();

    /**
     * Returns the daily overdue fine rate for this patron type (scale 2).
     */
    public abstract BigDecimal getDailyOverdueFine();

    /**
     * Calculates the overdue fine for the given number of overdue days using this patron's
     * daily rate and injected {@link FineCalculationStrategy}.
     */
    public BigDecimal calculateOverdueFine(long overdueDays) {
        Validation.requireNonNegative(overdueDays, "overdueDays");
        return Validation.normalizeMoney(
                fineStrategy.calculateFine(overdueDays, getDailyOverdueFine()),
                "fine"
        );
    }

    /**
     * Adds an assessed fine amount to this patron's composed {@link FineAccount}.
     */
    public void assessFine(BigDecimal amount) {
        fineAccount.assessFine(amount);
    }

    /**
     * Applies a fine payment to this patron's composed {@link FineAccount}.
     */
    public FinePayment payFine(String paymentId, BigDecimal amount, LocalDate paymentDate) {
        return fineAccount.pay(paymentId, amount, paymentDate);
    }

    public BigDecimal getFineBalance() {
        return fineAccount.getBalance();
    }

    public BigDecimal getTotalFinesPaid() {
        return fineAccount.getTotalPaid();
    }

    public List<FinePayment> getPaymentHistory() {
        return fineAccount.getPaymentHistory();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Patron patron = (Patron) o;
        return Objects.equals(id, patron.id)
                && Objects.equals(name, patron.name)
                && Objects.equals(email, patron.email)
                && Objects.equals(fineStrategy, patron.fineStrategy)
                && Objects.equals(fineAccount, patron.fineAccount);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getClass(), id, name, email, fineStrategy, fineAccount);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName()
                + "{id='" + id + "'"
                + ", name='" + name + "'"
                + ", email='" + email + "'"
                + ", borrowingLimit=" + getBorrowingLimit()
                + ", dailyOverdueFine=" + getDailyOverdueFine()
                + ", fineBalance=" + fineAccount.getBalance()
                + "}";
    }
}
