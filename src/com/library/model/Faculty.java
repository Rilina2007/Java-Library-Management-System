package com.library.model;

public class Faculty extends Member {
    private static final long serialVersionUID = 1L;

    public Faculty(int id, String name, String email) {
        super(id, name, email);
    }

    @Override public int getBorrowLimit() { return 5; }
    @Override public double getFinePerDay() { return 1.0; }
    @Override public String getType() { return "FACULTY"; }
}
