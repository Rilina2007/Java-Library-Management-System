package com.library.exception;

public class LimitExceededException extends LibraryException {
    public LimitExceededException(String name, int limit) {
        super(name + " has already borrowed the maximum of " + limit + " books.");
    }
}
