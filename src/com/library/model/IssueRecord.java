package com.library.model;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class IssueRecord implements Reportable {
    private final int id;
    private final int bookId;
    private final int memberId;
    private final String bookTitle;
    private final String memberName;
    private final LocalDate issueDate;
    private final LocalDate dueDate;
    private final LocalDate returnDate; // null while the book is still out
    private final double fine;

    public IssueRecord(int id, int bookId, int memberId, String bookTitle,
                       LocalDate issueDate, LocalDate dueDate, LocalDate returnDate) {
        this(id, bookId, memberId, bookTitle, "", issueDate, dueDate, returnDate, 0);
    }

    public IssueRecord(int id, int bookId, int memberId, String bookTitle, String memberName,
                       LocalDate issueDate, LocalDate dueDate, LocalDate returnDate, double fine) {
        this.id = id;
        this.bookId = bookId;
        this.memberId = memberId;
        this.bookTitle = bookTitle;
        this.memberName = memberName;
        this.issueDate = issueDate;
        this.dueDate = dueDate;
        this.returnDate = returnDate;
        this.fine = fine;
    }

    public int getId() { return id; }
    public int getBookId() { return bookId; }
    public int getMemberId() { return memberId; }
    public String getBookTitle() { return bookTitle; }
    public String getMemberName() { return memberName; }
    public LocalDate getIssueDate() { return issueDate; }
    public LocalDate getDueDate() { return dueDate; }
    public LocalDate getReturnDate() { return returnDate; }
    public double getFine() { return fine; }
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
