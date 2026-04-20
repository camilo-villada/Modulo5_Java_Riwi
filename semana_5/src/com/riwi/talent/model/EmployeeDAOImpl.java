package com.riwi.talent.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class EmployeeDAOImpl implements EmployeeDAO {

    private static final String SQL_CREATE_TABLE = """
            CREATE TABLE IF NOT EXISTS empleados (
                id INT AUTO_INCREMENT PRIMARY KEY,
                nombre VARCHAR(100) NOT NULL,
                cargo VARCHAR(100) NOT NULL,
                salario DOUBLE NOT NULL
            )
            """;
    private static final String SQL_INSERT = """
            INSERT INTO empleados(nombre, cargo, salario)
            VALUES (?, ?, ?)
            """;
    private static final String SQL_FIND_ALL = """
            SELECT
                e.id,
                e.nombre,
                e.cargo,
                e.salario,
                CASE
                    WHEN e.salario >= 8000 THEN 'Lead'
                    WHEN e.salario >= 4000 THEN 'Senior'
                    ELSE 'Junior'
                END AS nivel_salarial
            FROM empleados e
            WHERE e.salario >= 0
            ORDER BY e.cargo, e.nombre, e.id
            """;
    private static final String SQL_UPDATE = """
            UPDATE empleados
            SET nombre = ?, cargo = ?, salario = ?
            WHERE id = ?
            """;
    private static final String SQL_DELETE = """
            DELETE FROM empleados
            WHERE id = ?
            """;

    public EmployeeDAOImpl() {
        createTableIfNotExists();
    }

    @Override
    public boolean insert(Employee employee) {
        try (Connection connection = DatabaseConnection.connect();
             PreparedStatement ps = connection.prepareStatement(SQL_INSERT)) {

            ps.setString(1, employee.name());
            ps.setString(2, employee.role());
            ps.setDouble(3, employee.salary());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error inserting employee: " + e.getMessage());
            return false;
        }
    }

    @Override
    public List<EmployeeDTO> findAll() {
        List<EmployeeDTO> employees = new ArrayList<>();

        try (Connection connection = DatabaseConnection.connect();
             PreparedStatement ps = connection.prepareStatement(SQL_FIND_ALL);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                employees.add(mapEmployeeDTO(rs));
            }
        } catch (SQLException e) {
            System.out.println("Error listing employees: " + e.getMessage());
        }

        return employees;
    }

    @Override
    public boolean update(Employee employee) {
        try (Connection connection = DatabaseConnection.connect();
             PreparedStatement ps = connection.prepareStatement(SQL_UPDATE)) {

            ps.setString(1, employee.name());
            ps.setString(2, employee.role());
            ps.setDouble(3, employee.salary());
            ps.setInt(4, employee.id());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error updating employee: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean delete(int id) {
        try (Connection connection = DatabaseConnection.connect();
             PreparedStatement ps = connection.prepareStatement(SQL_DELETE)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error deleting employee: " + e.getMessage());
            return false;
        }
    }

    private void createTableIfNotExists() {
        try (Connection connection = DatabaseConnection.connect();
             PreparedStatement ps = connection.prepareStatement(SQL_CREATE_TABLE)) {

            ps.execute();
        } catch (SQLException e) {
            System.out.println("Could not validate the empleados table: " + e.getMessage());
        }
    }

    private EmployeeDTO mapEmployeeDTO(ResultSet rs) throws SQLException {
        /*
         * A record is used here as an immutable DTO for the result of a richer
         * SELECT query. Compared to a traditional Java 8 POJO, the record removes
         * boilerplate constructors, getters, equals and toString, so maintaining
         * JDBC mappings in Java 17/21 is faster and less error-prone.
         */
        return new EmployeeDTO(
                rs.getInt("id"),
                rs.getString("nombre"),
                rs.getString("cargo"),
                rs.getDouble("salario"),
                rs.getString("nivel_salarial")
        );
    }
}
