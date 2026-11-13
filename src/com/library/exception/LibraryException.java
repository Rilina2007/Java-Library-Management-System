package com.library.exception;

/** Base custom (checked) exception for all library rule violations. */
public class LibraryException extends Exception {
    public LibraryException(String message) {
        super(message);
    }
}
