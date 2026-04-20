package com.corporatetalenthub.service;

import com.corporatetalenthub.model.Employee;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class EmployeeRegistryService {

    // Week 3: fixed arrays were replaced by dynamic storage.
    private final ArrayList<Employee> employees;
    private final HashMap<String, Employee> employeesByCode;

    public EmployeeRegistryService() {
        this.employees = new ArrayList<>();
        this.employeesByCode = new HashMap<>();
    }

    public boolean addEmployee(Employee employee) {
        if (employee == null || employeesByCode.containsKey(employee.getEmployeeCode())) {
            return false;
        }

        employees.add(employee);
        employeesByCode.put(employee.getEmployeeCode(), employee);
        return true;
    }

    public List<Employee> listEmployees() {
        return new ArrayList<>(employees);
    }

    public Employee findByCode(String employeeCode) {
        return employeesByCode.get(employeeCode);
    }

    public boolean removeEmployee(String employeeCode) {
        Employee removed = employeesByCode.remove(employeeCode);
        if (removed == null) {
            return false;
        }

        employees.removeIf(employee -> employee.getEmployeeCode().equals(employeeCode));
        return true;
    }

    public int removeByMinimumScore(double minimumScore) {
        int totalBefore = employees.size();
        employees.removeIf(employee -> employee.getPerformanceScore() < minimumScore);
        employeesByCode.values().removeIf(employee -> employee.getPerformanceScore() < minimumScore);
        return totalBefore - employees.size();
    }

    // Legacy Java 8/11: manual index access.
    public Employee getFirstLegacy() {
        if (employees.isEmpty()) {
            return null;
        }
        return employees.get(0);
    }

    public Employee getLastLegacy() {
        if (employees.isEmpty()) {
            return null;
        }
        return employees.get(employees.size() - 1);
    }

    // Java 21: improved readability and less index-out-of-range risk.
    public Employee getFirstJava21() {
        if (employees.isEmpty()) {
            return null;
        }
        return employees.getFirst();
    }

    public Employee getLastJava21() {
        if (employees.isEmpty()) {
            return null;
        }
        return employees.getLast();
    }

    public List<Employee> listReversedJava21() {
        return new ArrayList<>(employees.reversed());
    }

    public int totalEmployees() {
        return employees.size();
    }

    public double averageSalary() {
        if (employees.isEmpty()) {
            return 0;
        }

        double sum = 0.0;
        for (Employee employee : employees) {
            sum += employee.getBaseSalary();
        }
        return sum / employees.size();
    }
}
