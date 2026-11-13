package com.library.db;

import com.library.model.Member;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class MemberDAO {

    public int add(String type, String name, String email) throws SQLException {
        String sql = "INSERT INTO members(name, email, type) VALUES (?,?,?)";
        try (Connection con = DBConnection.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, name);
            ps.setString(2, email);
            ps.setString(3, type.toUpperCase());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                return keys.next() ? keys.getInt(1) : -1;
            }
        }
    }

    public Member findById(int id) throws SQLException {
        try (Connection con = DBConnection.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement("SELECT * FROM members WHERE id = ?")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        }
    }

    public List<Member> findAll() throws SQLException {
        List<Member> list = new ArrayList<>();
        try (Connection con = DBConnection.getInstance().getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM members ORDER BY id")) {
            while (rs.next()) {
                list.add(map(rs));
            }
        }
        return list;
    }

    private Member map(ResultSet rs) throws SQLException {
        return Member.create(rs.getString("type"), rs.getInt("id"),
                rs.getString("name"), rs.getString("email"));
    }
}
