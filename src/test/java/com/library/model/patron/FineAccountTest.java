package com.library.model.patron;

import com.library.exception.InvalidFinePaymentException;
import com.library.exception.InvalidLibraryArgumentException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FineAccountTest {

    private static final LocalDate PAYMENT_DATE = LocalDate.of(2026, 4, 15);

    @Test
    void shouldStartWithZeroBalanceZeroTotalPaidAndEmptyPaymentHistory() {
        // Arrange
        FineAccount account = new FineAccount();

        // Act
        BigDecimal balance = account.getBalance();
        BigDecimal totalPaid = account.getTotalPaid();
        List<FinePayment> history = account.getPaymentHistory();

        // Assert
        assertEquals(new BigDecimal("0.00"), balance);
        assertEquals(new BigDecimal("0.00"), totalPaid);
        assertTrue(history.isEmpty());
    }

    @Test
    void shouldAccumulateNormalizedAssessmentsWhenAssessFineIsCalledMultipleTimes() {
        // Arrange
        FineAccount account = new FineAccount();

        // Act
        account.assessFine(new BigDecimal("1.5"));
        account.assessFine(new BigDecimal("0.75"));

        // Assert
        assertEquals(new BigDecimal("2.25"), account.getBalance());
    }

    @Test
    void shouldLeaveBalanceUnchangedWhenAssessFineReceivesZeroBoundary() {
        // Arrange
        FineAccount account = new FineAccount();
        account.assessFine(new BigDecimal("1.00"));

        // Act
        account.assessFine(BigDecimal.ZERO);

        // Assert
        assertEquals(new BigDecimal("1.00"), account.getBalance());
    }

    @Test
    void shouldThrowInvalidLibraryArgumentExceptionWhenAssessFineReceivesNull() {
        // Arrange
        FineAccount account = new FineAccount();

        // Act & Assert
        assertThrows(
                InvalidLibraryArgumentException.class,
                () -> account.assessFine(null)
        );
    }

    @Test
    void shouldThrowInvalidLibraryArgumentExceptionWhenAssessFineReceivesNegativeAmount() {
        // Arrange
        FineAccount account = new FineAccount();

        // Act & Assert
        assertThrows(
                InvalidLibraryArgumentException.class,
                () -> account.assessFine(new BigDecimal("-0.01"))
        );
    }

    @Test
    void shouldReduceBalanceAndRecordPaymentWhenPartialPaymentIsApplied() {
        // Arrange
        FineAccount account = new FineAccount();
        account.assessFine(new BigDecimal("3.50"));

        // Act
        FinePayment payment = account.pay("PAY-1", new BigDecimal("1.5"), PAYMENT_DATE);

        // Assert
        assertEquals(new FinePayment("PAY-1", new BigDecimal("1.50"), PAYMENT_DATE), payment);
        assertEquals(new BigDecimal("2.00"), account.getBalance());
        assertEquals(List.of(payment), account.getPaymentHistory());
    }

    @Test
    void shouldLeaveZeroBalanceWhenExactBalanceBoundaryIsPaid() {
        // Arrange
        FineAccount account = new FineAccount();
        account.assessFine(new BigDecimal("2.50"));

        // Act
        FinePayment payment = account.pay("PAY-1", new BigDecimal("2.50"), PAYMENT_DATE);

        // Assert
        assertEquals(new BigDecimal("2.50"), payment.amount());
        assertEquals(new BigDecimal("0.00"), account.getBalance());
    }

    @Test
    void shouldSumAllPaymentsUsingStreamWhenMultiplePaymentsAreMade() {
        // Arrange
        FineAccount account = new FineAccount();
        account.assessFine(new BigDecimal("5.00"));

        // Act
        account.pay("PAY-1", new BigDecimal("1.25"), PAYMENT_DATE);
        account.pay("PAY-2", new BigDecimal("2.75"), PAYMENT_DATE.plusDays(1));

        // Assert
        assertEquals(new BigDecimal("4.00"), account.getTotalPaid());
        assertEquals(new BigDecimal("1.00"), account.getBalance());
    }

    @Test
    void shouldReturnUnmodifiablePaymentHistorySnapshot() {
        // Arrange
        FineAccount account = new FineAccount();
        account.assessFine(new BigDecimal("2.00"));
        account.pay("PAY-1", new BigDecimal("1.00"), PAYMENT_DATE);
        List<FinePayment> history = account.getPaymentHistory();

        // Act & Assert
        assertThrows(
                UnsupportedOperationException.class,
                () -> history.add(new FinePayment("PAY-2", new BigDecimal("1.00"), PAYMENT_DATE))
        );
    }

    @Test
    void shouldThrowInvalidFinePaymentExceptionWhenPaymentAmountIsZeroBoundary() {
        // Arrange
        FineAccount account = new FineAccount();
        account.assessFine(new BigDecimal("2.00"));

        // Act & Assert
        assertThrows(
                InvalidFinePaymentException.class,
                () -> account.pay("PAY-1", BigDecimal.ZERO, PAYMENT_DATE)
        );
    }

    @Test
    void shouldThrowInvalidFinePaymentExceptionWhenPaymentAmountIsNegative() {
        // Arrange
        FineAccount account = new FineAccount();
        account.assessFine(new BigDecimal("2.00"));

        // Act & Assert
        assertThrows(
                InvalidFinePaymentException.class,
                () -> account.pay("PAY-1", new BigDecimal("-0.50"), PAYMENT_DATE)
        );
    }

    @Test
    void shouldThrowInvalidFinePaymentExceptionWhenSubCentPaymentRoundsToZero() {
        // Arrange
        FineAccount account = new FineAccount();
        account.assessFine(new BigDecimal("2.00"));

        // Act & Assert
        assertThrows(
                InvalidFinePaymentException.class,
                () -> account.pay("PAY-1", new BigDecimal("0.001"), PAYMENT_DATE)
        );
    }

    @Test
    void shouldThrowInvalidFinePaymentExceptionWhenPayingWithZeroBalance() {
        // Arrange
        FineAccount account = new FineAccount();

        // Act & Assert
        assertThrows(
                InvalidFinePaymentException.class,
                () -> account.pay("PAY-1", new BigDecimal("0.50"), PAYMENT_DATE)
        );
    }

    @Test
    void shouldThrowInvalidFinePaymentExceptionWhenPaymentExceedsBalanceByOneCent() {
        // Arrange
        FineAccount account = new FineAccount();
        account.assessFine(new BigDecimal("2.00"));

        // Act & Assert
        assertThrows(
                InvalidFinePaymentException.class,
                () -> account.pay("PAY-1", new BigDecimal("2.01"), PAYMENT_DATE)
        );
    }

    @Test
    void shouldThrowInvalidLibraryArgumentExceptionWhenPayReceivesBlankPaymentId() {
        // Arrange
        FineAccount account = new FineAccount();
        account.assessFine(new BigDecimal("2.00"));

        // Act & Assert
        assertThrows(
                InvalidLibraryArgumentException.class,
                () -> account.pay("   ", new BigDecimal("1.00"), PAYMENT_DATE)
        );
    }

    @Test
    void shouldThrowInvalidLibraryArgumentExceptionWhenPayReceivesNullAmount() {
        // Arrange
        FineAccount account = new FineAccount();
        account.assessFine(new BigDecimal("2.00"));

        // Act & Assert
        assertThrows(
                InvalidLibraryArgumentException.class,
                () -> account.pay("PAY-1", null, PAYMENT_DATE)
        );
    }

    @Test
    void shouldThrowInvalidLibraryArgumentExceptionWhenPayReceivesNullPaymentDate() {
        // Arrange
        FineAccount account = new FineAccount();
        account.assessFine(new BigDecimal("2.00"));

        // Act & Assert
        assertThrows(
                InvalidLibraryArgumentException.class,
                () -> account.pay("PAY-1", new BigDecimal("1.00"), null)
        );
    }

    @Test
    void shouldNormalizeAmountAndTrimIdWhenConstructingFinePaymentRecord() {
        // Arrange & Act
        FinePayment payment = new FinePayment("  PAY-7  ", new BigDecimal("3.5"), PAYMENT_DATE);

        // Assert
        assertEquals("PAY-7", payment.paymentId());
        assertEquals(new BigDecimal("3.50"), payment.amount());
        assertEquals(PAYMENT_DATE, payment.paymentDate());
    }

    @Test
    void shouldThrowInvalidFinePaymentExceptionWhenConstructingFinePaymentWithZeroAmount() {
        // Arrange
        BigDecimal zeroAmount = new BigDecimal("0.00");

        // Act & Assert
        assertThrows(
                InvalidFinePaymentException.class,
                () -> new FinePayment("PAY-1", zeroAmount, PAYMENT_DATE)
        );
    }

    @Test
    void shouldThrowInvalidLibraryArgumentExceptionWhenConstructingFinePaymentWithNullDate() {
        // Arrange
        LocalDate nullDate = null;

        // Act & Assert
        assertThrows(
                InvalidLibraryArgumentException.class,
                () -> new FinePayment("PAY-1", new BigDecimal("1.00"), nullDate)
        );
    }

    @Test
    void shouldConsiderTwoFineAccountsEqualByValueWhenBalanceAndPaymentsMatch() {
        // Arrange
        FineAccount first = new FineAccount();
        first.assessFine(new BigDecimal("3.00"));
        first.pay("PAY-1", new BigDecimal("1.00"), PAYMENT_DATE);

        FineAccount second = new FineAccount();
        second.assessFine(new BigDecimal("3.00"));
        second.pay("PAY-1", new BigDecimal("1.00"), PAYMENT_DATE);

        // Act & Assert
        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());
    }

    @Test
    void shouldConsiderTwoFineAccountsNotEqualWhenBalancesDiffer() {
        // Arrange
        FineAccount first = new FineAccount();
        first.assessFine(new BigDecimal("1.00"));

        FineAccount second = new FineAccount();
        second.assessFine(new BigDecimal("2.00"));

        // Act & Assert
        assertNotEquals(first, second);
    }

    @Test
    void shouldIncludeBalanceAndPaymentsInFineAccountToString() {
        // Arrange
        FineAccount account = new FineAccount();
        account.assessFine(new BigDecimal("2.50"));
        account.pay("PAY-1", new BigDecimal("1.00"), PAYMENT_DATE);

        // Act
        String description = account.toString();

        // Assert
        assertTrue(description.contains("1.50") && description.contains("PAY-1"));
    }
}
