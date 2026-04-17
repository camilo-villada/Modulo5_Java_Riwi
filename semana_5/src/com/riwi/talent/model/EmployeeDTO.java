package com.riwi.talent.model;

public record EmployeeDTO(int id, String name, String role, double salary, String salaryLevel) {

    public EmployeeDTO(int id, String name, String role, double salary) {
        this(id, name, role, salary, "Unclassified");
    }

    public String formattedSalary() {
        return "$ %.2f".formatted(salary);
    }
}
