package app;

import model.Developer;
import model.Employee;
import model.ExternalConsultant;
import model.Manager;
import model.Person;
import report.PerformanceReport;
import service.ProfileValidator;
import service.ReportService;

public class Main {

    public static void main(String[] args) {
        Employee developer = new Developer(101, "Camila", 5_000_000, "Java");
        Employee manager = new Manager(102, "Juan", 7_500_000, 90_000_000);
        Person consultant = new ExternalConsultant(201, "Laura");

        ProfileValidator profileValidator = new ProfileValidator();

        System.out.println("=== WEEK 4 | MODERN OOP ===");

        System.out.println("\nTASK 3 - Legacy validation:");
        System.out.println(profileValidator.validateLegacy(developer));
        System.out.println(profileValidator.validateLegacy(manager));
        System.out.println(profileValidator.validateLegacy(consultant));

        System.out.println("\nTASK 3 - Modern validation (Pattern Matching):");
        System.out.println(profileValidator.validateModern(developer));
        System.out.println(profileValidator.validateModern(manager));
        System.out.println(profileValidator.validateModern(consultant));

        // TASK 2: immutable record integrated into the end-of-month flow.
        ReportService reportService = new ReportService();
        PerformanceReport developerReport = reportService.createEndOfMonthReport(developer, 4.8);
        PerformanceReport managerReport = reportService.createEndOfMonthReport(manager, 4.4);

        System.out.println("\nTASK 2 - End-of-month reports (record):");
        System.out.println(developerReport);
        System.out.println(managerReport);

        // TASK 4: interface default method for non-breaking evolution.
        double developerBonus = developer.calculatePromotionBonus(developerReport.average());
        developer.logOperation("Bonus for " + developer.getName() + ": " + developerBonus);

        double managerBonus = manager.calculatePromotionBonus(managerReport.average());
        manager.logOperation("Bonus for " + manager.getName() + ": " + managerBonus);
    }
}
