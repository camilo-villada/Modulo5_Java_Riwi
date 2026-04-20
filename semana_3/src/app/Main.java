package app;

import config.LocationsConfig;
import model.Employee;
import service.EmployeeService;
import utils.ReportUtils;

public class Main {

    public static void main(String[] args) {
        var employeeService = new EmployeeService();

        System.out.println("=== CORPORATE TALENT HUB | WEEK 3 ===");
        System.out.println("Technologies (List.of): " + LocationsConfig.TECHNOLOGIES);
        System.out.println("Locations (Map.of): " + LocationsConfig.LOCATIONS);

        employeeService.addEmployee(new Employee("COD-001", "Camila", "Java", "BOG", 4500000, 92));
        employeeService.addEmployee(new Employee("COD-002", "Juan", "SQL", "MED", 3900000, 74));
        employeeService.addEmployee(new Employee("COD-003", "Laura", "React", "CAL", 4100000, 81));
        employeeService.addEmployee(new Employee("COD-004", "Andres", "Spring Boot", "BAR", 5200000, 88));

        ReportUtils.printEmployees("Initial list (ArrayList)", employeeService.listEmployees());

        var searchResult = employeeService.findById("COD-003");
        System.out.println("\nFast HashMap search (COD-003): " + searchResult);

        var removed = employeeService.removeEmployee("COD-002");
        System.out.println("Removal by id COD-002: " + (removed ? "ok" : "not found"));
        ReportUtils.printEmployees("List after removing by id", employeeService.listEmployees());

        // Collection evolution: Java 8/11 uses indexes; Java 21 adds direct methods.
        System.out.println("\nFirst employee Legacy (get(0)): " + employeeService.getFirstLegacy());
        System.out.println("Last employee Legacy (get(size-1)): " + employeeService.getLastLegacy());
        System.out.println("First employee Java 21 (getFirst): " + employeeService.getFirstJava21());
        System.out.println("Last employee Java 21 (getLast): " + employeeService.getLastJava21());
        ReportUtils.printEmployees("Java 21 reversed order (reversed)", employeeService.listReversedJava21());

        var removedByScore = employeeService.removeByMinimumScore(85);
        System.out.println("\nFiltering with removeIf. Removed by score < 85: " + removedByScore);
        ReportUtils.printEmployees("List after filtering", employeeService.listEmployees());

        ReportUtils.printFinalReport(employeeService);

        // Java 11+: var reduces visual noise in loops and temporary variables.
        // Java 8: explicit types were always required, for example "Employee employee".
    }
}
