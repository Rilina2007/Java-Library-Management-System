package com.library.service;

import com.library.db.BookDAO;
import com.library.db.DBConnection;
import com.library.db.IssueDAO;
import com.library.db.MemberDAO;
import com.library.exception.BookNotAvailableException;
import com.library.exception.LibraryException;
import com.library.exception.LimitExceededException;
import com.library.exception.NotFoundException;
import com.library.model.Book;
import com.library.model.IssueRecord;
import com.library.model.Member;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

/** Business logic layer: enforces the library rules and talks to the DAOs. */
public class LibraryService {
    private final BookDAO bookDAO = new BookDAO();
    private final MemberDAO memberDAO = new MemberDAO();
    private final IssueDAO issueDAO = new IssueDAO();

    // ---------- Method overloading: same name, different parameters ----------
    public int addBook(String title, String author) throws SQLException {
        return addBook(new Book(title, author));
    }

    public int addBook(String title, String author, String category, int copies) throws SQLException {
        return addBook(new Book(title, author, category, copies));
    }

    public int addBook(Book book) throws SQLException {
        int id = bookDAO.add(book);
        FileManager.log("Book added: #" + id + " " + book.getTitle());
        return id;
    }

    public int registerMember(String type, String name, String email) throws SQLException {
        int id = memberDAO.add(type, name, email);
        FileManager.log("Member registered: #" + id + " " + name + " (" + type.toUpperCase() + ")");
        return id;
    }

    public List<Book> allBooks() throws SQLException { return bookDAO.findAll(); }
    public List<Book> searchBooks(String keyword) throws SQLException { return bookDAO.search(keyword); }
    public List<Member> allMembers() throws SQLException { return memberDAO.findAll(); }
    public List<IssueRecord> allIssues() throws SQLException { return issueDAO.findAll(); }
    public List<IssueRecord> overdue() throws SQLException { return issueDAO.findOverdue(LocalDate.now()); }
    public List<IssueRecord> activeIssues(int memberId) throws SQLException {
        return issueDAO.findActiveForMember(memberId);
    }

    public void removeBook(int id) throws SQLException, NotFoundException {
        if (bookDAO.findById(id) == null) {
            throw new NotFoundException("Book", id);
        }
        bookDAO.delete(id);
        FileManager.log("Book removed: #" + id);
    }

    /**
     * Issues a book. 'synchronized' so two threads can never issue the last copy twice.
     * Uses a JDBC transaction: either both the issue row AND the stock update happen, or neither.
     */
    public synchronized void issueBook(int memberId, int bookId, int days) throws LibraryException, SQLException {
        Member member = memberDAO.findById(memberId);
        if (member == null) {
            throw new NotFoundException("Member", memberId);
        }
        Book book = bookDAO.findById(bookId);
        if (book == null) {
            throw new NotFoundException("Book", bookId);
        }
        if (!book.isAvailable()) {
            throw new BookNotAvailableException(book.getTitle());
        }
        if (issueDAO.countActiveForMember(memberId) >= member.getBorrowLimit()) {
            throw new LimitExceededException(member.getName(), member.getBorrowLimit());
        }

        LocalDate today = LocalDate.now();
        try (Connection con = DBConnection.getInstance().getConnection()) {
            con.setAutoCommit(false);
            try {
                issueDAO.insert(con, bookId, memberId, today, today.plusDays(days));
                bookDAO.changeAvailability(con, bookId, -1);
                con.commit();
            } catch (SQLException e) {
                con.rollback();
                throw e;
            }
        }
        FileManager.log("Issued '" + book.getTitle() + "' to " + member.getName() + ", due " + today.plusDays(days));
    }

    /** Returns a book and gives back the fine (0 if on time). Fine rate depends on member type. */
    public synchronized double returnBook(int issueId) throws LibraryException, SQLException {
        IssueRecord rec = issueDAO.findById(issueId);
        if (rec == null || rec.isReturned()) {
            throw new NotFoundException("Active issue record", issueId);
        }
        Member member = memberDAO.findById(rec.getMemberId());
        LocalDate today = LocalDate.now();
        double fine = rec.daysOverdue(today) * member.getFinePerDay(); // polymorphic call

        try (Connection con = DBConnection.getInstance().getConnection()) {
            con.setAutoCommit(false);
            try {
                issueDAO.markReturned(con, issueId, today, fine);
                bookDAO.changeAvailability(con, rec.getBookId(), +1);
                con.commit();
            } catch (SQLException e) {
                con.rollback();
                throw e;
            }
        }
        FileManager.log("Returned issue #" + issueId + ", fine Rs." + fine);
        return fine;
    }
}
