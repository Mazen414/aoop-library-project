package com.library.exception;

/**
 * Thrown when a fine payment is non-positive, exceeds the current balance,
 * or is attempted when no fine balance is owed.
 */
public class InvalidFinePaymentException extends LibraryException {

    private static final long serialVersionUID = 1L;

    public InvalidFinePaymentException(String message) {
        super(message);
    }
}
