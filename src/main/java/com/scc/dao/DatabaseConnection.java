package com.scc.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConnection {
    private static final String DB_URL = "jdbc:sqlite:studentRecords.db";

    public static Connection getConnection() throws SQLException {
        Connection con = DriverManager.getConnection(DB_URL);
        try (Statement stmt = con.createStatement()) {
            stmt.execute("PRAGMA foreign_keys = ON;");
        } catch (SQLException e) {
            System.out.println("Error enabling foreign keys: " + e.getMessage());
        }
        return con;
    }
}
