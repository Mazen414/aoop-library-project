package com.library.exception;

/**
 * Thrown when a requested library item cannot be found in the catalog.
 */
public class ItemNotFoundException extends LibraryException {

    private static final long serialVersionUID = 1L;

    public ItemNotFoundException(String message) {
        super(message);
    }
}
