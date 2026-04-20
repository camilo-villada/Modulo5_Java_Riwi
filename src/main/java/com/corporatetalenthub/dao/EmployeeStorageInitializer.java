package com.corporatetalenthub.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class EmployeeStorageInitializer {

    private static final String SQL_CREATE_TABLE = """
            CREATE TABLE IF NOT EXISTS employees (
                id INT AUTO_INCREMENT PRIMARY KEY,
                name VARCHAR(100) NOT NULL,
                role VARCHAR(100) NOT NULL,
                salary DOUBLE NOT NULL
            )
            """;

    public void initialize() {
        try (Connection connection = DatabaseConnection.connect();
             PreparedStatement statement = connection.prepareStatement(SQL_CREATE_TABLE)) {

            statement.execute();
        } catch (SQLException exception) {
            throw new EmployeeDataAccessException("Could not initialize employees storage", exception);
        }
    }
}
