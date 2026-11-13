package com.library.model;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class IssueRecord implements Reportable {
    private final int id;
    private final int bookId;
    private final int memberId;
    private final String bookTitle;
    private final LocalDate issueDate;
    private final LocalDate dueDate;
    private final LocalDate returnDate; // null while the book is still out

    public IssueRecord(int id, int bookId, int memberId, String bookTitle,
                       LocalDate issueDate, LocalDate dueDate, LocalDate returnDate) {
        this.id = id;
        this.bookId = bookId;
        this.memberId = memberId;
        this.bookTitle = bookTitle;
        this.issueDate = issueDate;
        this.dueDate = dueDate;
        this.returnDate = returnDate;
    }

    public int getId() { return id; }
    public int getBookId() { return bookId; }
    public int getMemberId() { return memberId; }
    public LocalDate getDueDate() { return dueDate; }
    public boolean isReturned() { return returnDate != null; }

    public long daysOverdue(LocalDate on) {
        return Math.max(0, ChronoUnit.DAYS.between(dueDate, on));
    }

    @Override
    public String toReportLine() {
        return String.format("#%-4d book=%-25s member=%-4d issued=%s due=%s %s",
                id, bookTitle, memberId, issueDate, dueDate,
                isReturned() ? "returned " + returnDate : "NOT RETURNED");
    }

    @Override
    public String toString() { return toReportLine(); }
}
