package com.riwi.talent.model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class DatabaseConnection {

    private static final String DEFAULT_URL = "jdbc:mysql://127.0.0.1:3306/curso_jdbc";
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
     * In many legacy Java 8 projects, connections were opened with
     * try-catch-finally and then Connection, Statement and ResultSet were closed
     * manually inside finally:
     *
     * Connection connection = null;
     * PreparedStatement statement = null;
     * ResultSet resultSet = null;
     * try {
     *     // JDBC logic
     * } finally {
     *     // close resultSet, statement and connection one by one
     * }
     *
     * That approach worked, but it was easy to forget a close call, duplicate
     * cleanup logic, or trigger NullPointerException checks during manual closing.
     *
     * With try-with-resources, Java closes JDBC resources automatically when the
     * block finishes. This helps prevent memory leaks and connection leaks,
     * because the application does not leave open objects consuming heap memory,
     * cursors, or database sockets after each operation. In modern LTS versions
     * such as Java 17/21, this reduces boilerplate and makes the data layer easier
     * to maintain than the traditional Java 8 POJO plus manual-finally approach.
     */
}
