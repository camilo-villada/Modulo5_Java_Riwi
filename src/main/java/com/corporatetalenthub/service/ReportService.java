package com.corporatetalenthub.service;

import com.corporatetalenthub.model.Employee;
import com.corporatetalenthub.model.PerformanceFeedback;
import com.corporatetalenthub.model.PerformanceReport;

public class ReportService {

    public PerformanceReport createEndOfMonthReport(Employee employee, double average) {
        PerformanceFeedback feedback;
        if (average >= 4.5) {
            feedback = PerformanceFeedback.OUTSTANDING;
        } else if (average >= 3.5) {
            feedback = PerformanceFeedback.EXPECTED;
        } else {
            feedback = PerformanceFeedback.IMPROVEMENT_PLAN;
        }
        return new PerformanceReport(employee.getId(), average, feedback);
    }
}
