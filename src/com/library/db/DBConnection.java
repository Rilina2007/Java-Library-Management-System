package com.library.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * JDBC connection manager (Singleton).
 * Default: SQLite file database (no server needed).
 * To use MySQL instead, change URL/USER/PASSWORD below and add the MySQL connector jar.
 */
public class DBConnection {
    // ---- SQLite (default) ----
    private static final String URL = "jdbc:sqlite:library.db";
    private static final String USER = null;
    private static final String PASSWORD = null;
    // ---- MySQL (alternative) ----
    // private static final String URL = "jdbc:mysql://localhost:3306/librarydb";
    // private static final String USER = "root";
    // private static final String PASSWORD = "your_password";

    private static DBConnection instance;

    private DBConnection() { }

    public static synchronized DBConnection getInstance() {
        if (instance == null) {
            instance = new DBConnection();
        }
        return instance;
    }

    public Connection getConnection() throws SQLException {
        Connection con = (USER == null)
                ? DriverManager.getConnection(URL)
                : DriverManager.getConnection(URL, USER, PASSWORD);
        try (Statement st = con.createStatement()) {
            st.execute("PRAGMA foreign_keys = ON"); // ignored/unsupported on MySQL -> remove if using MySQL
        } catch (SQLException ignore) { }
        return con;
    }

    /** Creates the tables on first run. */
    public void initSchema() throws SQLException {
        try (Connection con = getConnection(); Statement st = con.createStatement()) {
            st.execute("CREATE TABLE IF NOT EXISTS books ("
                    + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + "title TEXT NOT NULL, author TEXT NOT NULL, category TEXT, "
                    + "total INTEGER NOT NULL, available INTEGER NOT NULL)");
            st.execute("CREATE TABLE IF NOT EXISTS members ("
                    + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + "name TEXT NOT NULL, email TEXT, type TEXT NOT NULL)");
            st.execute("CREATE TABLE IF NOT EXISTS issues ("
                    + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + "book_id INTEGER NOT NULL REFERENCES books(id), "
                    + "member_id INTEGER NOT NULL REFERENCES members(id), "
                    + "issue_date TEXT NOT NULL, due_date TEXT NOT NULL, "
                    + "return_date TEXT, fine REAL DEFAULT 0)");
        }
    }
}
