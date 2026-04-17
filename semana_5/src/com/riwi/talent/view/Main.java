package com.riwi.talent.view;

import com.riwi.talent.controller.EmployeeController;

import java.util.Scanner;

public class Main {

    private final Scanner scanner;
    private final EmployeeController controller;

    public Main() {
        this.scanner = new Scanner(System.in);
        this.controller = new EmployeeController();
    }

    public static void main(String[] args) {
        new Main().start();
    }

    public void start() {
        int option;

        do {
            showMenu();
            option = readInt("Select an option: ");
            processOption(option);
        } while (option != 0);

        scanner.close();
    }

    private void showMenu() {
        System.out.println("""
                ==============================
                     MVC EMPLOYEE CRUD
                ==============================
                1. Insert employee
                2. List employees
                3. Update employee
                4. Delete employee
                0. Exit
                """);
    }

    private void processOption(int option) {
        switch (option) {
            case 1 -> insertEmployee();
            case 2 -> listEmployees();
            case 3 -> updateEmployee();
            case 4 -> deleteEmployee();
            case 0 -> System.out.println("Application closed.");
            default -> System.out.println("Invalid option. Please try again.");
        }
    }

    private void insertEmployee() {
        System.out.println("\n--- Insert employee ---");

        String name = readText("Name: ");
        String role = readText("Role: ");
        double salary = readDouble("Salary: ");

        System.out.println(controller.insertEmployee(name, role, salary));
    }

    private void listEmployees() {
        System.out.println("\n--- Employee list ---");
        System.out.println(controller.generateReport());
    }

    private void updateEmployee() {
        System.out.println("\n--- Update employee ---");

        int id = readInt("Employee id: ");
        String name = readText("New name: ");
        String role = readText("New role: ");
        double salary = readDouble("New salary: ");

        System.out.println(controller.updateEmployee(id, name, role, salary));
    }

    private void deleteEmployee() {
        System.out.println("\n--- Delete employee ---");

        int id = readInt("Employee id to delete: ");
        System.out.println(controller.deleteEmployee(id));
    }

    private int readInt(String message) {
        while (true) {
            System.out.print(message);
            String value = scanner.nextLine();

            try {
                return Integer.parseInt(value);
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid integer number.");
            }
        }
    }

    private double readDouble(String message) {
        while (true) {
            System.out.print(message);
            String value = scanner.nextLine();

            try {
                return Double.parseDouble(value);
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid decimal number.");
            }
        }
    }

    private String readText(String message) {
        while (true) {
            System.out.print(message);
            String value = scanner.nextLine().trim();

            if (!value.isEmpty()) {
                return value;
            }

            System.out.println("This field cannot be empty.");
        }
    }
}
