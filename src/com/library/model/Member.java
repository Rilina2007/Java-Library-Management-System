package com.library.model;

import java.io.Serializable;

/** Abstract class: common data for every library member; subclasses define limits & fines. */
public abstract class Member implements Reportable, Serializable {
    private static final long serialVersionUID = 1L;

    protected int id;
    protected String name;
    protected String email;

    protected Member(int id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }

    // Abstract methods -> each subclass must override (runtime polymorphism)
    public abstract int getBorrowLimit();
    public abstract double getFinePerDay();
    public abstract String getType();

    public int getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }

    /** Factory method: builds the right subclass from the type stored in the database. */
    public static Member create(String type, int id, String name, String email) {
        if ("FACULTY".equalsIgnoreCase(type)) {
            return new Faculty(id, name, email);
        }
        return new Student(id, name, email);
    }

    @Override
    public String toReportLine() {
        return String.format("%-4d %-20s %-25s %-8s limit=%d fine=Rs.%.1f/day",
                id, name, email, getType(), getBorrowLimit(), getFinePerDay());
    }

    @Override
    public String toString() { return toReportLine(); }
}
