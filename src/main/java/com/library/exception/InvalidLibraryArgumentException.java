package com.library.exception;

/**
 * Thrown when a required method argument or constructor parameter is null, blank, or out of valid bounds.
 */
public class InvalidLibraryArgumentException extends LibraryException {

    private static final long serialVersionUID = 1L;

    public InvalidLibraryArgumentException(String message) {
        super(message);
    }
}
