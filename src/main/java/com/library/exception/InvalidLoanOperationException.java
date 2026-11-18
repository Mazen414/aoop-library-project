package com.library.exception;

/**
 * Thrown when a loan operation violates lifecycle or date invariants
 * (for example, returning an already-returned loan or supplying a date before the borrow date).
 */
public class InvalidLoanOperationException extends LibraryException {

    private static final long serialVersionUID = 1L;

    public InvalidLoanOperationException(String message) {
        super(message);
    }
}
