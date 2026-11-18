package com.library.exception;

/**
 * Abstract base exception for all domain and validation errors in the library management system.
 * Declared abstract so callers always throw a specific, descriptive domain exception subclass.
 */
public abstract class LibraryException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    protected LibraryException(String message) {
        super(message);
    }
}
