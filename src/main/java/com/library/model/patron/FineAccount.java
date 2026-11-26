package com.library.model.patron;

import com.library.exception.InvalidFinePaymentException;
import com.library.util.Validation;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Tracks a single patron's outstanding fine balance and immutable payment history.
 * Owned exclusively via composition by {@link Patron}.
 */
public final class FineAccount {

    private BigDecimal balance;
    private final List<FinePayment> payments;

    public FineAccount() {
        this.balance = Validation.ZERO_MONEY;
        this.payments = new ArrayList<>();
    }

    /**
     * Adds a non-negative fine amount to the account's outstanding balance.
     */
    public void assessFine(BigDecimal amount) {
        BigDecimal normalized = Validation.requireNonNegativeMoney(amount, "amount");
        this.balance = Validation.normalizeMoney(this.balance.add(normalized), "balance");
    }

    /**
     * Applies a fine payment against the outstanding balance on the given deterministic date.
     *
     * @throws InvalidFinePaymentException if the payment is non-positive, the balance is zero,
     *                                     or the payment exceeds the current balance
     */
    public FinePayment pay(String paymentId, BigDecimal amount, LocalDate paymentDate) {
        String validPaymentId = Validation.requireNonBlank(paymentId, "paymentId");
        Validation.requireNonNull(amount, "amount");
        Validation.requireNonNull(paymentDate, "paymentDate");

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidFinePaymentException("Payment amount must be positive, but was: " + amount);
        }
        BigDecimal normalizedAmount = Validation.normalizeMoney(amount, "amount");
        if (normalizedAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidFinePaymentException("Payment amount must be at least 0.01, but was: " + amount);
        }
        if (this.balance.compareTo(BigDecimal.ZERO) == 0) {
            throw new InvalidFinePaymentException("Cannot process payment when no fine balance is owed.");
        }
        if (normalizedAmount.compareTo(this.balance) > 0) {
            throw new InvalidFinePaymentException(
                    "Payment amount " + normalizedAmount + " exceeds current fine balance of " + this.balance + "."
            );
        }

        FinePayment payment = new FinePayment(validPaymentId, normalizedAmount, paymentDate);
        this.balance = Validation.normalizeMoney(this.balance.subtract(normalizedAmount), "balance");
        this.payments.add(payment);
        return payment;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    /**
     * Returns the cumulative sum of all recorded fine payments using a side-effect-free Stream.
     */
    public BigDecimal getTotalPaid() {
        return payments.stream()
                .map(FinePayment::amount)
                .reduce(Validation.ZERO_MONEY, BigDecimal::add);
    }

    /**
     * Returns an unmodifiable snapshot of the payment history.
     */
    public List<FinePayment> getPaymentHistory() {
        return List.copyOf(payments);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        FineAccount that = (FineAccount) o;
        return Objects.equals(balance, that.balance)
                && Objects.equals(payments, that.payments);
    }

    @Override
    public int hashCode() {
        return Objects.hash(balance, payments);
    }

    @Override
    public String toString() {
        return "FineAccount{balance=" + balance + ", payments=" + payments + "}";
    }
}
