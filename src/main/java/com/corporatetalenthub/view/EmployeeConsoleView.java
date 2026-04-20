package com.corporatetalenthub.view;

import com.corporatetalenthub.controller.EmployeeController;
import com.corporatetalenthub.model.EmployeeListResult;
import com.corporatetalenthub.model.EmployeeOperationStatus;
import com.corporatetalenthub.model.EmployeeReportRow;
import com.corporatetalenthub.model.SalaryLevel;
import com.corporatetalenthub.utils.ConsoleReader;

public class EmployeeConsoleView {

    private final ConsoleReader consoleReader;
    private final EmployeeController employeeController;

    public EmployeeConsoleView(ConsoleReader consoleReader) {
        this.consoleReader = consoleReader;
        this.employeeController = new EmployeeController();
    }

    public void start() {
        int option;

        do {
            showMenu();
            option = consoleReader.readInt("Select an option: ");
            processOption(option);
        } while (option != 0);
    }

    public String previewReport() {
        return buildEmployeeReport(employeeController.listEmployees());
    }

    private void showMenu() {
        System.out.println("""
                ==============================
                   CORPORATE TALENT HUB CRUD
                ==============================
                1. Insert employee
                2. List employees
                3. Update employee
                4. Delete employee
                0. Return to main menu
                """);
    }

    private void processOption(int option) {
        switch (option) {
            case 1 -> insertEmployee();
            case 2 -> listEmployees();
            case 3 -> updateEmployee();
            case 4 -> deleteEmployee();
            case 0 -> System.out.println("Returning to the main menu.");
            default -> System.out.println("Invalid option. Please try again.");
        }
    }

    private void insertEmployee() {
        System.out.println("\n--- Insert employee ---");
        String name = consoleReader.readNonBlank("Name: ");
        String role = consoleReader.readNonBlank("Role: ");
        double salary = consoleReader.readDouble("Salary: ");
        System.out.println(buildOperationMessage(employeeController.insertEmployee(name, role, salary), "insert"));
    }

    private void listEmployees() {
        System.out.println("\n--- Employee list ---");
        System.out.println(buildEmployeeReport(employeeController.listEmployees()));
    }

    private void updateEmployee() {
        System.out.println("\n--- Update employee ---");
        int id = consoleReader.readInt("Employee id: ");
        String name = consoleReader.readNonBlank("New name: ");
        String role = consoleReader.readNonBlank("New role: ");
        double salary = consoleReader.readDouble("New salary: ");
        System.out.println(buildOperationMessage(employeeController.updateEmployee(id, name, role, salary), "update"));
    }

    private void deleteEmployee() {
        System.out.println("\n--- Delete employee ---");
        int id = consoleReader.readInt("Employee id to delete: ");
        System.out.println(buildOperationMessage(employeeController.deleteEmployee(id), "delete"));
    }

    private String buildOperationMessage(EmployeeOperationStatus status, String operation) {
        return switch (status) {
            case SUCCESS -> switch (operation) {
                case "insert" -> "Employee inserted successfully.";
                case "update" -> "Employee updated successfully.";
                case "delete" -> "Employee deleted successfully.";
                default -> "Operation completed successfully.";
            };
            case INVALID_ID -> "The employee id must be greater than 0.";
            case INVALID_NAME -> "The employee name cannot be empty.";
            case INVALID_ROLE -> "The employee role cannot be empty.";
            case INVALID_SALARY -> "The salary must be greater than 0.";
            case NOT_FOUND -> switch (operation) {
                case "update" -> "No employee was found with that id to update.";
                case "delete" -> "No employee was found with that id to delete.";
                default -> "The requested employee was not found.";
            };
            case STORAGE_ERROR -> "The employee operation could not be completed because the database is unavailable.";
        };
    }

    private String buildEmployeeReport(EmployeeListResult result) {
        if (!result.storageAvailable()) {
            return """
                    ==============================
                    EMPLOYEE GENERAL REPORT
                    ==============================
                    The database is unavailable.
                    """;
        }

        if (result.employees().isEmpty()) {
            return """
                    ==============================
                    EMPLOYEE GENERAL REPORT
                    ==============================
                    There are no employees registered.
                    """;
        }

        StringBuilder detail = new StringBuilder();
        for (EmployeeReportRow employee : result.employees()) {
            detail.append("%-5d %-20s %-20s %-12s %-10s%n".formatted(
                    employee.id(),
                    employee.name(),
                    employee.role(),
                    formatSalary(employee.salary()),
                    formatSalaryLevel(employee.salaryLevel())
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

    private String formatSalary(double salary) {
        return "$ %.2f".formatted(salary);
    }

    private String formatSalaryLevel(SalaryLevel salaryLevel) {
        return switch (salaryLevel) {
            case JUNIOR -> "Junior";
            case SENIOR -> "Senior";
            case LEAD -> "Lead";
        };
    }
}
