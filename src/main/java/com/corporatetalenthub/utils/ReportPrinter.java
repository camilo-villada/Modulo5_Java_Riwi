package com.corporatetalenthub.utils;

import com.corporatetalenthub.model.Employee;
import com.corporatetalenthub.service.EmployeeRegistryService;

import java.util.List;

public final class ReportPrinter {

    private ReportPrinter() {
    }

    public static void printEmployees(String title, List<Employee> employees) {
        System.out.println("\n" + title);
        if (employees.isEmpty()) {
            System.out.println("No records found.");
            return;
        }

        for (Employee employee : employees) {
            System.out.println(employee);
        }
    }

    public static void printFinalReport(EmployeeRegistryService employeeRegistryService) {
        int total = employeeRegistryService.totalEmployees();
        double average = employeeRegistryService.averageSalary();

        System.out.println("\n=== FINAL REPORT ===");
        System.out.println("Total employees: " + total);
        System.out.println("Average salary: " + average);
    }
}
