package com.library.db;

import com.library.model.IssueRecord;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class IssueDAO {

    private static final String SELECT =
            "SELECT i.*, b.title FROM issues i JOIN books b ON b.id = i.book_id ";

    public void insert(Connection con, int bookId, int memberId, LocalDate issue, LocalDate due)
            throws SQLException {
        try (PreparedStatement ps = con.prepareStatement(
                "INSERT INTO issues(book_id, member_id, issue_date, due_date) VALUES (?,?,?,?)")) {
            ps.setInt(1, bookId);
            ps.setInt(2, memberId);
            ps.setString(3, issue.toString());
            ps.setString(4, due.toString());
            ps.executeUpdate();
        }
    }

    public void markReturned(Connection con, int issueId, LocalDate returned, double fine)
            throws SQLException {
        try (PreparedStatement ps = con.prepareStatement(
                "UPDATE issues SET return_date = ?, fine = ? WHERE id = ?")) {
            ps.setString(1, returned.toString());
            ps.setDouble(2, fine);
            ps.setInt(3, issueId);
            ps.executeUpdate();
        }
    }

    public IssueRecord findById(int id) throws SQLException {
        try (Connection con = DBConnection.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(SELECT + "WHERE i.id = ?")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        }
    }

    public int countActiveForMember(int memberId) throws SQLException {
        try (Connection con = DBConnection.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(
                     "SELECT COUNT(*) FROM issues WHERE member_id = ? AND return_date IS NULL")) {
            ps.setInt(1, memberId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        }
    }

    public List<IssueRecord> findActiveForMember(int memberId) throws SQLException {
        return list(SELECT + "WHERE i.member_id = ? AND i.return_date IS NULL ORDER BY i.id", memberId);
    }

    public List<IssueRecord> findOverdue(LocalDate today) throws SQLException {
        List<IssueRecord> out = new ArrayList<>();
        try (Connection con = DBConnection.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(
                     SELECT + "WHERE i.return_date IS NULL AND i.due_date < ? ORDER BY i.due_date")) {
            ps.setString(1, today.toString());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    out.add(map(rs));
                }
            }
        }
        return out;
    }

    public List<IssueRecord> findAll() throws SQLException {
        return list(SELECT + "ORDER BY i.id", null);
    }

    private List<IssueRecord> list(String sql, Integer param) throws SQLException {
        List<IssueRecord> out = new ArrayList<>();
        try (Connection con = DBConnection.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            if (param != null) {
                ps.setInt(1, param);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    out.add(map(rs));
                }
            }
        }
        return out;
    }

    private IssueRecord map(ResultSet rs) throws SQLException {
        String ret = rs.getString("return_date");
        return new IssueRecord(rs.getInt("id"), rs.getInt("book_id"), rs.getInt("member_id"),
                rs.getString("title"), LocalDate.parse(rs.getString("issue_date")),
                LocalDate.parse(rs.getString("due_date")), ret == null ? null : LocalDate.parse(ret));
    }
}
