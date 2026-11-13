package com.library.exception;

public class BookNotAvailableException extends LibraryException {
    public BookNotAvailableException(String title) {
        super("All copies of '" + title + "' are currently issued.");
    }
}
