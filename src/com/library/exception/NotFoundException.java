package com.library.exception;

public class NotFoundException extends LibraryException {
    public NotFoundException(String what, int id) {
        super(what + " with ID " + id + " was not found.");
    }
}
