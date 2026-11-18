package com.library.exception;

/**
 * Thrown when a requested patron cannot be found in the library registry.
 */
public class PatronNotFoundException extends LibraryException {

    private static final long serialVersionUID = 1L;

    public PatronNotFoundException(String message) {
        super(message);
    }
}
