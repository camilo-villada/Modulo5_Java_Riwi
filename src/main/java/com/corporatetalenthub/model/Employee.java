package com.corporatetalenthub.model;

public sealed abstract class Employee extends Person implements Promotable permits Developer, Manager {

    private final String employeeCode;
    private final double baseSalary;
    private final String locationCode;
    private final double performanceScore;

    protected Employee(int id,
                       String employeeCode,
                       String name,
                       double baseSalary,
                       String locationCode,
                       double performanceScore) {
        super(id, name);
        this.employeeCode = employeeCode;
        this.baseSalary = baseSalary;
        this.locationCode = locationCode;
        this.performanceScore = performanceScore;
    }

    public String getEmployeeCode() {
        return employeeCode;
    }

    public double getBaseSalary() {
        return baseSalary;
    }

    public String getLocationCode() {
        return locationCode;
    }

    public double getPerformanceScore() {
        return performanceScore;
    }

    public String getRoleName() {
        return getClass().getSimpleName();
    }

    @Override
    public String toString() {
        return "Employee{"
                + "id=" + getId()
                + ", code='" + employeeCode + '\''
                + ", name='" + getName() + '\''
                + ", role='" + getRoleName() + '\''
                + ", location='" + locationCode + '\''
                + ", salary=" + baseSalary
                + ", performanceScore=" + performanceScore
                + '}';
    }
}
