package com.library.model;

public class Book implements Reportable {
    private int id;
    private String title;
    private String author;
    private String category;
    private int total;
    private int available;

    // Constructor overloading
    public Book(String title, String author) {
        this(title, author, "General", 1);
    }

    public Book(String title, String author, String category, int copies) {
        this(0, title, author, category, copies, copies);
    }

    public Book(int id, String title, String author, String category, int total, int available) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.category = category;
        this.total = total;
        this.available = available;
    }

    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public String getCategory() { return category; }
    public int getTotal() { return total; }
    public int getAvailable() { return available; }
    public boolean isAvailable() { return available > 0; }

    @Override
    public String toReportLine() {
        return String.format("%-4d %-30s %-20s %-12s %d/%d", id, title, author, category, available, total);
    }

    @Override
    public String toString() { return toReportLine(); }
}
