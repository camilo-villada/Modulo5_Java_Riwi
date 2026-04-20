package utils;

import model.Employee;
import service.EmployeeService;

import java.util.List;

public class ReportUtils {

    private ReportUtils() {
    }

    public static void printEmployees(String title, List<Employee> employees) {
        System.out.println("\n" + title);
        if (employees.isEmpty()) {
            System.out.println("No records found.");
            return;
        }

        for (var employee : employees) {
            System.out.println(employee);
        }
    }

    public static void printFinalReport(EmployeeService employeeService) {
        var total = employeeService.totalEmployees();
        var average = employeeService.averageSalary();

        System.out.println("\n=== FINAL REPORT ===");
        System.out.println("Total employees: " + total);
        System.out.println("Average salary: " + average);
    }
}
