package com.corporatetalenthub.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class DatabaseConnection {

    private static final String DEFAULT_URL = "jdbc:mysql://127.0.0.1:3306/corporate_talent_hub";
    private static final String DEFAULT_USER = "root";
    private static final String DEFAULT_PASSWORD = "1234";

    private DatabaseConnection() {
    }

    public static Connection connect() throws SQLException {
        String url = readSetting("db.url", "DB_URL", DEFAULT_URL);
        String user = readSetting("db.user", "DB_USER", DEFAULT_USER);
        String password = readSetting("db.password", "DB_PASSWORD", DEFAULT_PASSWORD);

        return DriverManager.getConnection(url, user, password);
    }

    private static String readSetting(String systemProperty, String environmentVariable, String defaultValue) {
        String propertyValue = System.getProperty(systemProperty);
        if (propertyValue != null && !propertyValue.isBlank()) {
            return propertyValue;
        }

        String environmentValue = System.getenv(environmentVariable);
        if (environmentValue != null && !environmentValue.isBlank()) {
            return environmentValue;
        }

        return defaultValue;
    }

    /*
     * In many legacy Java 8 projects, JDBC resources were opened with try-catch-finally
     * and closed manually one by one inside finally blocks.
     *
     * With try-with-resources, Java closes Connection, PreparedStatement and ResultSet
     * automatically when the block ends. This reduces boilerplate and helps prevent
     * memory leaks, cursor leaks and open database sockets in modern LTS versions.
     */
}
