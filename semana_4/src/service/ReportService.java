package service;

import model.Employee;
import report.PerformanceReport;

public class ReportService {

    public PerformanceReport createEndOfMonthReport(Employee employee, double average) {
        String feedback;
        if (average >= 4.5) {
            feedback = "Outstanding performance";
        } else if (average >= 3.5) {
            feedback = "Expected performance";
        } else {
            feedback = "Needs an improvement plan";
        }
        return new PerformanceReport(employee.getId(), average, feedback);
    }
}
