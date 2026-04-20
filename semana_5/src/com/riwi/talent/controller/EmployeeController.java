package com.riwi.talent.controller;

import com.riwi.talent.model.Employee;
import com.riwi.talent.model.EmployeeDAO;
import com.riwi.talent.model.EmployeeDAOImpl;
import com.riwi.talent.model.EmployeeDTO;

import java.util.List;

public class EmployeeController {

    private final EmployeeDAO employeeDAO;

    public EmployeeController() {
        this.employeeDAO = new EmployeeDAOImpl();
    }

    public String insertEmployee(String name, String role, double salary) {
        Employee employee = new Employee(name, role, salary);
        boolean inserted = employeeDAO.insert(employee);

        return inserted
                ? "Employee inserted successfully."
                : "Could not insert the employee.";
    }

    public List<EmployeeDTO> listEmployees() {
        return employeeDAO.findAll();
    }

    public String updateEmployee(int id, String name, String role, double salary) {
        Employee employee = new Employee(id, name, role, salary);
        boolean updated = employeeDAO.update(employee);

        return updated
                ? "Employee updated successfully."
                : "No employee was found with that id to update.";
    }

    public String deleteEmployee(int id) {
        boolean deleted = employeeDAO.delete(id);

        return deleted
                ? "Employee deleted successfully."
                : "No employee was found with that id to delete.";
    }

    public String generateReport() {
        List<EmployeeDTO> employees = listEmployees();

        if (employees.isEmpty()) {
            return """
                    ==============================
                    EMPLOYEE GENERAL REPORT
                    ==============================
                    There are no employees registered.
                    """;
        }

        StringBuilder detail = new StringBuilder();

        for (EmployeeDTO employee : employees) {
            detail.append("%-5d %-20s %-20s %-12s %-10s%n".formatted(
                    employee.id(),
                    employee.name(),
                    employee.role(),
                    employee.formattedSalary(),
                    employee.salaryLevel()
            ));
        }

        return """
                ==============================
                EMPLOYEE GENERAL REPORT
                ==============================
                %-5s %-20s %-20s %-12s %-10s
                %s
                """.formatted("ID", "NAME", "ROLE", "SALARY", "LEVEL", detail);
    }
}
