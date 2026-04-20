package com.corporatetalenthub.model;

public class EmployeeProfile {

    private byte riskLevel;
    private short pendingVacationDays;
    private int employeeId;
    private long contractNumber;
    private float monthlyBonus;
    private double baseSalary;
    private char category;
    private boolean active;
    private String name;

    public EmployeeProfile(byte riskLevel,
                           short pendingVacationDays,
                           int employeeId,
                           long contractNumber,
                           float monthlyBonus,
                           double baseSalary,
                           char category,
                           boolean active,
                           String name) {
        this.riskLevel = riskLevel;
        this.pendingVacationDays = pendingVacationDays;
        this.employeeId = employeeId;
        this.contractNumber = contractNumber;
        this.monthlyBonus = monthlyBonus;
        this.baseSalary = baseSalary;
        this.category = category;
        this.active = active;
        this.name = name;
    }

    public double calculateFinalSalary() {
        // Precedence applied: parentheses first, then multiplication, then addition/subtraction.
        return (baseSalary + (monthlyBonus * 1.10)) - (baseSalary * 0.05);
    }

    public boolean hasEvenId() {
        return employeeId % 2 == 0;
    }

    public boolean validateEligibility(int testScore, int age, int siteId) {
        // Precedence applied: ! first, then &&, and finally ||.
        return (testScore > 85 && age < 30) || (siteId == 1 && !active);
    }

    public void addBonus(float extra) {
        monthlyBonus += extra;
    }

    public String quickSummary() {
        return String.format(
                "Employee %s | category %s | contract %d | active %s | risk %d | vacations %d",
                name, category, contractNumber, active, riskLevel, pendingVacationDays
        );
    }
}
