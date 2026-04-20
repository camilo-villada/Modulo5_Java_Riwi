package com.corporatetalenthub.app;

import com.corporatetalenthub.model.CompanyRecord;
import com.corporatetalenthub.model.Developer;
import com.corporatetalenthub.model.Employee;
import com.corporatetalenthub.model.EmployeeProfile;
import com.corporatetalenthub.model.ExternalConsultant;
import com.corporatetalenthub.model.Manager;
import com.corporatetalenthub.model.PerformanceReport;
import com.corporatetalenthub.model.PerformanceSummary;
import com.corporatetalenthub.model.Person;
import com.corporatetalenthub.model.ProfileValidationResult;
import com.corporatetalenthub.model.RegistrationResult;
import com.corporatetalenthub.model.RegistrationValidationResult;
import com.corporatetalenthub.model.SalaryCategory;
import com.corporatetalenthub.service.ArchitectureNotesService;
import com.corporatetalenthub.service.EmployeeRegistryService;
import com.corporatetalenthub.service.ProfileValidator;
import com.corporatetalenthub.service.ReportService;
import com.corporatetalenthub.service.TalentFlowService;
import com.corporatetalenthub.utils.ConsoleReader;
import com.corporatetalenthub.utils.LocationsConfig;
import com.corporatetalenthub.utils.ReportPrinter;
import com.corporatetalenthub.view.EmployeeConsoleView;

import java.util.List;

public class CorporateTalentHubApplication {

    private final ConsoleReader consoleReader;
    private final ArchitectureNotesService architectureNotesService;
    private final TalentFlowService talentFlowService;
    private final ProfileValidator profileValidator;
    private final ReportService reportService;
    private final EmployeeConsoleView employeeConsoleView;

    public CorporateTalentHubApplication() {
        this.consoleReader = new ConsoleReader();
        this.architectureNotesService = new ArchitectureNotesService();
        this.talentFlowService = new TalentFlowService();
        this.profileValidator = new ProfileValidator();
        this.reportService = new ReportService();
        this.employeeConsoleView = new EmployeeConsoleView(consoleReader);
    }

    public static void main(String[] args) {
        new CorporateTalentHubApplication().run(args);
    }

    public void run(String[] args) {
        if (args.length > 0 && "--demo".equalsIgnoreCase(args[0])) {
            runDemo();
            return;
        }

        runInteractiveMenu();
    }

    private void runInteractiveMenu() {
        int option;

        do {
            showMainMenu();
            option = consoleReader.readInt("Select a module: ");
            switch (option) {
                case 1 -> runWeekOneFoundations();
                case 2 -> runWeekTwoFlow();
                case 3 -> runWeekThreeCollections();
                case 4 -> runWeekFourAdvancedOop();
                case 5 -> employeeConsoleView.start();
                case 0 -> System.out.println("Corporate Talent Hub closed.");
                default -> System.out.println("Invalid option.");
            }
        } while (option != 0);
    }

    private void runDemo() {
        System.out.println("""
                ===============================
                Corporate Talent Hub
                Unified Evolution Demo
                ===============================
                """);

        runWeekOneFoundations();

        RegistrationResult registration = talentFlowService.simulateRegistration("Camila", 25, 4500);
        System.out.println("\n=== WEEK 2 | CONTROL FLOW AND VALIDATIONS ===");
        System.out.println(buildRegistrationSummary(registration));
        printPerformanceSummary();

        runWeekThreeCollections();
        runWeekFourAdvancedOop();

        System.out.println("\n=== WEEK 5 | JDBC + MVC ===");
        System.out.println(employeeConsoleView.previewReport());
    }

    private void showMainMenu() {
        System.out.println("""

                ===============================
                Corporate Talent Hub
                ===============================
                1. Week 1 foundations
                2. Week 2 control flow and validations
                3. Week 3 collections framework
                4. Week 4 advanced OOP
                5. Week 5 JDBC + MVC
                0. Exit
                """);
    }

    private void runWeekOneFoundations() {
        System.out.println("""
                ===============================
                Corporate Talent Hub - Week 1
                ===============================
                """);

        for (String note : architectureNotesService.getSummaryLines()) {
            System.out.println(note);
        }

        EmployeeProfile camila = new EmployeeProfile(
                (byte) 3,
                (short) 12,
                1024,
                8900123L,
                250000.50f,
                3200000.00,
                'A',
                true,
                "Camila"
        );

        camila.addBonus(15000.0f);

        System.out.println(camila.quickSummary());
        System.out.println("Final salary: " + camila.calculateFinalSalary());
        System.out.println("Even ID: " + camila.hasEvenId());
        System.out.println("Eligible: " + camila.validateEligibility(90, 24, 2));

        CompanyRecord company = new CompanyRecord("Corporate Talent Hub", "900.123.456-7", 2020);
        System.out.println("Company record: " + company);

        // In Java 14+, the NPE message explains more precisely which reference was null.
        String corporateEmail = null;
        try {
            System.out.println(corporateEmail.toUpperCase());
        } catch (NullPointerException exception) {
            System.out.println("Captured null case: " + exception.getMessage());
        }

        String areaA = new String("Backend");
        String areaB = new String("Backend");
        System.out.println("Comparison with == : " + (areaA == areaB));
        System.out.println("Comparison with equals: " + areaA.equals(areaB));
    }

    private void runWeekTwoFlow() {
        System.out.println("\n=== WEEK 2 | CONTROL FLOW AND VALIDATIONS ===");

        String name = consoleReader.readNonBlank("Enter name: ");
        int age = consoleReader.readInt("Enter age (18-65): ");
        double salary = consoleReader.readDouble("Enter salary: ");

        RegistrationValidationResult registrationResult = talentFlowService.registerEmployee(name, age, salary);
        System.out.println(buildRegistrationMessage(registrationResult));

        printPerformanceSummary();
    }

    private void printPerformanceSummary() {
        for (PerformanceSummary summary : talentFlowService.processPerformance()) {
            System.out.println();
            System.out.println(buildPerformanceSummary(summary));
        }

        /*
         * Java 8:
         *   Exception messages were generic and not very descriptive.
         *
         * Java 17/21:
         *   Helpful NullPointerExceptions indicate exactly which reference caused the error.
         *   This reduces debugging time in production and classroom exercises.
         */
    }

    private void runWeekThreeCollections() {
        EmployeeRegistryService employeeRegistryService = buildWeekThreeRegistry();

        System.out.println("\n=== CORPORATE TALENT HUB | WEEK 3 ===");
        System.out.println("Technologies (List.of): " + LocationsConfig.TECHNOLOGIES);
        System.out.println("Locations (Map.of): " + LocationsConfig.LOCATIONS);

        ReportPrinter.printEmployees("Initial list (ArrayList)", employeeRegistryService.listEmployees());

        Employee searchResult = employeeRegistryService.findByCode("COD-003");
        System.out.println("\nFast HashMap search (COD-003): " + searchResult);

        boolean removed = employeeRegistryService.removeEmployee("COD-002");
        System.out.println("Removal by code COD-002: " + (removed ? "ok" : "not found"));
        ReportPrinter.printEmployees("List after removing by code", employeeRegistryService.listEmployees());

        System.out.println("\nFirst employee Legacy (get(0)): " + employeeRegistryService.getFirstLegacy());
        System.out.println("Last employee Legacy (get(size-1)): " + employeeRegistryService.getLastLegacy());
        System.out.println("First employee Java 21 (getFirst): " + employeeRegistryService.getFirstJava21());
        System.out.println("Last employee Java 21 (getLast): " + employeeRegistryService.getLastJava21());
        ReportPrinter.printEmployees("Java 21 reversed order (reversed)", employeeRegistryService.listReversedJava21());

        int removedByScore = employeeRegistryService.removeByMinimumScore(85);
        System.out.println("\nFiltering with removeIf. Removed by score < 85: " + removedByScore);
        ReportPrinter.printEmployees("List after filtering", employeeRegistryService.listEmployees());
        ReportPrinter.printFinalReport(employeeRegistryService);
    }

    private EmployeeRegistryService buildWeekThreeRegistry() {
        EmployeeRegistryService employeeRegistryService = new EmployeeRegistryService();
        List<Employee> employees = List.of(
                new Developer(101, "COD-001", "Camila", 4_500_000, "BOG", 92, "Java"),
                new Manager(102, "COD-002", "Juan", 3_900_000, "MED", 74, 45_000_000),
                new Developer(103, "COD-003", "Laura", 4_100_000, "CAL", 81, "React"),
                new Developer(104, "COD-004", "Andres", 5_200_000, "BAR", 88, "Spring Boot")
        );

        for (Employee employee : employees) {
            employeeRegistryService.addEmployee(employee);
        }

        return employeeRegistryService;
    }

    private void runWeekFourAdvancedOop() {
        Employee developer = new Developer(101, "COD-001", "Camila", 5_000_000, "BOG", 95, "Java");
        Employee manager = new Manager(102, "COD-002", "Juan", 7_500_000, "MED", 89, 90_000_000);
        Person consultant = new ExternalConsultant(201, "Laura");

        System.out.println("\n=== WEEK 4 | MODERN OOP ===");
        System.out.println("\nTASK 3 - Legacy validation:");
        System.out.println(formatProfileValidation("Legacy", profileValidator.validateLegacy(developer)));
        System.out.println(formatProfileValidation("Legacy", profileValidator.validateLegacy(manager)));
        System.out.println(formatProfileValidation("Legacy", profileValidator.validateLegacy(consultant)));

        System.out.println("\nTASK 3 - Modern validation (Pattern Matching):");
        System.out.println(formatProfileValidation("Modern", profileValidator.validateModern(developer)));
        System.out.println(formatProfileValidation("Modern", profileValidator.validateModern(manager)));
        System.out.println(formatProfileValidation("Modern", profileValidator.validateModern(consultant)));

        PerformanceReport developerReport = reportService.createEndOfMonthReport(developer, 4.8);
        PerformanceReport managerReport = reportService.createEndOfMonthReport(manager, 4.4);

        System.out.println("\nTASK 2 - End-of-month reports (record):");
        System.out.println(formatPerformanceReport(developerReport));
        System.out.println(formatPerformanceReport(managerReport));

        double developerBonus = developer.calculatePromotionBonus(developerReport.average());
        developer.logOperation("Bonus for " + developer.getName() + ": " + developerBonus);

        double managerBonus = manager.calculatePromotionBonus(managerReport.average());
        manager.logOperation("Bonus for " + manager.getName() + ": " + managerBonus);
    }

    private String buildRegistrationMessage(RegistrationValidationResult result) {
        return switch (result.status()) {
            case SUCCESS -> buildRegistrationSummary(result.registration());
            case INVALID_NAME -> "The name cannot be empty.";
            case INVALID_AGE -> "Age out of range. Must be between 18 and 65.";
            case INVALID_SALARY -> "Salary must be greater than 0.";
        };
    }

    private String buildRegistrationSummary(RegistrationResult registration) {
        return """
                Registered employee: %s
                Salary category: %s
                """.formatted(registration.name(), formatSalaryCategory(registration.salaryCategory())).stripTrailing();
    }

    private String buildPerformanceSummary(PerformanceSummary summary) {
        return """
                Employee %d
                Actual average: %.2f
                Simplified score: %d
                Status: %s
                """.formatted(
                summary.employeeNumber(),
                summary.average(),
                summary.simplifiedScore(),
                summary.promoted() ? "Promoted" : "Not promoted"
        ).stripTrailing();
    }

    private String formatProfileValidation(String mode, ProfileValidationResult result) {
        return switch (result.type()) {
            case DEVELOPER -> "%s -> Developer with primary language %s".formatted(mode, result.detail());
            case MANAGER -> "%s -> Manager with monthly budget %s".formatted(mode, result.detail());
            case GENERAL -> "%s -> Profile without promotion rules".formatted(mode);
        };
    }

    private String formatPerformanceReport(PerformanceReport report) {
        return "PerformanceReport[employeeId=%d, average=%.1f, feedback=%s]".formatted(
                report.employeeId(),
                report.average(),
                formatFeedback(report.feedback())
        );
    }

    private String formatFeedback(com.corporatetalenthub.model.PerformanceFeedback feedback) {
        return switch (feedback) {
            case OUTSTANDING -> "Outstanding performance";
            case EXPECTED -> "Expected performance";
            case IMPROVEMENT_PLAN -> "Needs an improvement plan";
        };
    }

    private String formatSalaryCategory(SalaryCategory salaryCategory) {
        return switch (salaryCategory) {
            case LOW -> "Low";
            case MEDIUM -> "Medium";
            case HIGH -> "High";
            case PREMIUM -> "Premium";
        };
    }
}
