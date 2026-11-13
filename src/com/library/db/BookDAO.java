package com.library.db;

import com.library.model.Book;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/** Data Access Object: all SQL for the books table lives here. */
public class BookDAO {

    public int add(Book b) throws SQLException {
        String sql = "INSERT INTO books(title, author, category, total, available) VALUES (?,?,?,?,?)";
        try (Connection con = DBConnection.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, b.getTitle());
            ps.setString(2, b.getAuthor());
            ps.setString(3, b.getCategory());
            ps.setInt(4, b.getTotal());
            ps.setInt(5, b.getAvailable());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                return keys.next() ? keys.getInt(1) : -1;
            }
        }
    }

    public Book findById(int id) throws SQLException {
        try (Connection con = DBConnection.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement("SELECT * FROM books WHERE id = ?")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        }
    }

    public List<Book> findAll() throws SQLException {
        return query("SELECT * FROM books ORDER BY id", null);
    }

    /** Searches title OR author (partial, case-insensitive). */
    public List<Book> search(String keyword) throws SQLException {
        return query("SELECT * FROM books WHERE LOWER(title) LIKE ? OR LOWER(author) LIKE ? ORDER BY id",
                "%" + keyword.toLowerCase() + "%");
    }

    public void delete(int id) throws SQLException {
        try (Connection con = DBConnection.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement("DELETE FROM books WHERE id = ?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    /** Used inside a transaction: the caller passes its own connection. */
    public void changeAvailability(Connection con, int bookId, int delta) throws SQLException {
        try (PreparedStatement ps = con.prepareStatement(
                "UPDATE books SET available = available + ? WHERE id = ?")) {
            ps.setInt(1, delta);
            ps.setInt(2, bookId);
            ps.executeUpdate();
        }
    }

    private List<Book> query(String sql, String param) throws SQLException {
        List<Book> list = new ArrayList<>();
        try (Connection con = DBConnection.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            if (param != null) {
                ps.setString(1, param);
                ps.setString(2, param);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(map(rs));
                }
            }
        }
        return list;
    }

    private Book map(ResultSet rs) throws SQLException {
        return new Book(rs.getInt("id"), rs.getString("title"), rs.getString("author"),
                rs.getString("category"), rs.getInt("total"), rs.getInt("available"));
    }
}
