package service;

import model.Employee;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class EmployeeService {

    // Week 3: fixed arrays were replaced by dynamic storage.
    private final ArrayList<Employee> employees;
    private final HashMap<String, Employee> employeesById;

    public EmployeeService() {
        this.employees = new ArrayList<>();
        this.employeesById = new HashMap<>();
    }

    public boolean addEmployee(Employee employee) {
        if (employee == null || employeesById.containsKey(employee.getId())) {
            return false;
        }

        employees.add(employee);
        employeesById.put(employee.getId(), employee);
        return true;
    }

    public List<Employee> listEmployees() {
        return new ArrayList<>(employees);
    }

    public Employee findById(String id) {
        return employeesById.get(id);
    }

    public boolean removeEmployee(String id) {
        var removed = employeesById.remove(id);
        if (removed == null) {
            return false;
        }

        employees.removeIf(employee -> employee.getId().equals(id));
        return true;
    }

    public int removeByMinimumScore(double minimumScore) {
        var totalBefore = employees.size();
        employees.removeIf(employee -> employee.getPerformanceScore() < minimumScore);
        employeesById.values().removeIf(employee -> employee.getPerformanceScore() < minimumScore);
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

    // Java 21: better readability and less index-out-of-range risk.
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

        var sum = 0.0;
        for (var employee : employees) {
            sum += employee.getSalary();
        }
        return sum / employees.size();
    }
}
