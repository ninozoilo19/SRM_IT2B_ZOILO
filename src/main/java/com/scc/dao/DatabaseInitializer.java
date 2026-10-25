package com.scc.dao;

public class DatabaseInitializer {
    public static void initialize() {

        UserDao userDao = new UserDao();
        System.out.println("Database initialized successfully.");
    }
}