package com.library.exception;

/**
 * Thrown when a requested loan cannot be found in the lending records.
 */
public class LoanNotFoundException extends LibraryException {

    private static final long serialVersionUID = 1L;

    public LoanNotFoundException(String message) {
        super(message);
    }
}
