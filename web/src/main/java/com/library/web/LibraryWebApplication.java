package com.library.web;

import com.library.db.DBConnection;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.sql.SQLException;

@SpringBootApplication
public class LibraryWebApplication {

    public static void main(String[] args) throws SQLException {
        DBConnection.getInstance().initSchema();
        SpringApplication.run(LibraryWebApplication.class, args);
    }
}
