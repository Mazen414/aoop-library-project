package com.library.model.patron;

import com.library.exception.InvalidFinePaymentException;
import com.library.util.Validation;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Immutable value record representing a single fine payment made by a patron.
 * Monetary amounts are canonically normalized to scale 2 ({@link java.math.RoundingMode#HALF_UP}).
 */
public record FinePayment(String paymentId, BigDecimal amount, LocalDate paymentDate) {

    public FinePayment {
        paymentId = Validation.requireNonBlank(paymentId, "paymentId");
        Validation.requireNonNull(amount, "amount");
        Validation.requireNonNull(paymentDate, "paymentDate");

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidFinePaymentException("Payment amount must be positive, but was: " + amount);
        }
        BigDecimal normalizedAmount = Validation.normalizeMoney(amount, "amount");
        if (normalizedAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidFinePaymentException("Payment amount must be at least 0.01, but was: " + amount);
        }
        amount = normalizedAmount;
    }
}
