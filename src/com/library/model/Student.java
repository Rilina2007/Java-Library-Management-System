package com.library.model;

public class Student extends Member {
    private static final long serialVersionUID = 1L;

    public Student(int id, String name, String email) {
        super(id, name, email);
    }

    @Override public int getBorrowLimit() { return 3; }
    @Override public double getFinePerDay() { return 2.0; }
    @Override public String getType() { return "STUDENT"; }
}
