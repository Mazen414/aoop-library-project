package com.library.exception;

/**
 * Thrown when attempting to register a patron whose identifier already exists in the registry.
 */
public class DuplicatePatronException extends LibraryException {

    private static final long serialVersionUID = 1L;

    public DuplicatePatronException(String message) {
        super(message);
    }
}
