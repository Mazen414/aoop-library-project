package com.library.exception;

/**
 * Thrown when attempting to add a library item whose identifier already exists in the catalog.
 */
public class DuplicateItemException extends LibraryException {

    private static final long serialVersionUID = 1L;

    public DuplicateItemException(String message) {
        super(message);
    }
}
