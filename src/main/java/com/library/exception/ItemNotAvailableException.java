package com.library.exception;

/**
 * Thrown when attempting to borrow a library item that is currently checked out.
 */
public class ItemNotAvailableException extends LibraryException {

    private static final long serialVersionUID = 1L;

    public ItemNotAvailableException(String message) {
        super(message);
    }
}
