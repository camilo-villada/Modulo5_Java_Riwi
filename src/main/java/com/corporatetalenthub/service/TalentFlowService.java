package com.corporatetalenthub.service;

import com.corporatetalenthub.model.PerformanceSummary;
import com.corporatetalenthub.model.RegistrationResult;
import com.corporatetalenthub.model.RegistrationValidationResult;
import com.corporatetalenthub.model.SalaryCategory;

import java.util.ArrayList;
import java.util.List;

public class TalentFlowService {

    public SalaryCategory getSalaryCategory(double salary) {
        return switch ((int) salary / 1000) {
            case 1, 2 -> SalaryCategory.LOW;
            case 3, 4 -> SalaryCategory.MEDIUM;
            case 5, 6 -> SalaryCategory.HIGH;
            default -> SalaryCategory.PREMIUM;
        };
    }

    public RegistrationValidationResult registerEmployee(String name, int age, double salary) {
        if (name == null || name.isBlank()) {
            return new RegistrationValidationResult(RegistrationValidationResult.Status.INVALID_NAME, null);
        }
        if (age < 18 || age > 65) {
            return new RegistrationValidationResult(RegistrationValidationResult.Status.INVALID_AGE, null);
        }
        if (salary <= 0) {
            return new RegistrationValidationResult(RegistrationValidationResult.Status.INVALID_SALARY, null);
        }

        RegistrationResult registration = new RegistrationResult(name.trim(), age, salary, getSalaryCategory(salary));
        return new RegistrationValidationResult(RegistrationValidationResult.Status.SUCCESS, registration);
    }

    public RegistrationResult simulateRegistration(String name, int age, double salary) {
        return registerEmployee(name, age, salary).registration();
    }

    public List<PerformanceSummary> processPerformance() {
        double[][] ratings = {
                {4.5, 3.8, 4.2},
                {3.0, 3.5, 4.0}
        };

        List<PerformanceSummary> summary = new ArrayList<>();

        for (int i = 0; i < ratings.length; i++) {
            double sum = 0;

            for (int j = 0; j < ratings[i].length; j++) {
                sum += ratings[i][j];
            }

            double average = sum / ratings[i].length;
            int averageInt = (int) average;
            boolean promoted = average >= 3.5;

            summary.add(new PerformanceSummary(i + 1, average, averageInt, promoted));
        }

        return summary;
    }
}
