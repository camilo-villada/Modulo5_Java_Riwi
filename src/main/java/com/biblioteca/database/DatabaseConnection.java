package com.biblioteca.database;

public class DatabaseConnection {
    
    private static final String URL = "jdbc:mysql://localhost:3306/biblioteca";
    private static final String USER = "root";
    private static final String PASSWORD = "1234";

    public static java.sql.Connection getConnection() throws java.sql.SQLException {
        return java.sql.DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
