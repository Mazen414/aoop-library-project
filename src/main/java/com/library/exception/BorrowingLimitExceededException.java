package com.library.exception;

/**
 * Thrown when a patron attempts to borrow an item after reaching their maximum active loan limit.
 */
public class BorrowingLimitExceededException extends LibraryException {

    private static final long serialVersionUID = 1L;

    public BorrowingLimitExceededException(String message) {
        super(message);
    }
}
