package com.corporatetalenthub.dao;

import com.corporatetalenthub.model.PersistedEmployee;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class JdbcEmployeeDao implements EmployeeDao {

    private static final String SQL_INSERT = """
            INSERT INTO employees(name, role, salary)
            VALUES (?, ?, ?)
            """;
    private static final String SQL_FIND_ALL = """
            SELECT id, name, role, salary
            FROM employees
            ORDER BY role, name, id
            """;
    private static final String SQL_UPDATE = """
            UPDATE employees
            SET name = ?, role = ?, salary = ?
            WHERE id = ?
            """;
    private static final String SQL_DELETE = """
            DELETE FROM employees
            WHERE id = ?
            """;

    @Override
    public boolean insert(PersistedEmployee employee) {
        try (Connection connection = DatabaseConnection.connect();
             PreparedStatement statement = connection.prepareStatement(SQL_INSERT)) {

            statement.setString(1, employee.name());
            statement.setString(2, employee.role());
            statement.setDouble(3, employee.salary());
            return statement.executeUpdate() > 0;
        } catch (SQLException exception) {
            throw new EmployeeDataAccessException("Error inserting employee", exception);
        }
    }

    @Override
    public List<PersistedEmployee> findAll() {
        List<PersistedEmployee> employees = new ArrayList<>();

        try (Connection connection = DatabaseConnection.connect();
             PreparedStatement statement = connection.prepareStatement(SQL_FIND_ALL);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                employees.add(mapEmployee(resultSet));
            }
        } catch (SQLException exception) {
            throw new EmployeeDataAccessException("Error listing employees", exception);
        }

        return employees;
    }

    @Override
    public boolean update(PersistedEmployee employee) {
        try (Connection connection = DatabaseConnection.connect();
             PreparedStatement statement = connection.prepareStatement(SQL_UPDATE)) {

            statement.setString(1, employee.name());
            statement.setString(2, employee.role());
            statement.setDouble(3, employee.salary());
            statement.setInt(4, employee.id());
            return statement.executeUpdate() > 0;
        } catch (SQLException exception) {
            throw new EmployeeDataAccessException("Error updating employee", exception);
        }
    }

    @Override
    public boolean delete(int id) {
        try (Connection connection = DatabaseConnection.connect();
             PreparedStatement statement = connection.prepareStatement(SQL_DELETE)) {

            statement.setInt(1, id);
            return statement.executeUpdate() > 0;
        } catch (SQLException exception) {
            throw new EmployeeDataAccessException("Error deleting employee", exception);
        }
    }

    private PersistedEmployee mapEmployee(ResultSet resultSet) throws SQLException {
        return new PersistedEmployee(
                resultSet.getInt("id"),
                resultSet.getString("name"),
                resultSet.getString("role"),
                resultSet.getDouble("salary")
        );
    }
}
