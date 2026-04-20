package com.corporatetalenthub.model;

public final class Developer extends Employee {

    private final String primaryLanguage;

    public Developer(int id,
                     String employeeCode,
                     String name,
                     double baseSalary,
                     String locationCode,
                     double performanceScore,
                     String primaryLanguage) {
        super(id, employeeCode, name, baseSalary, locationCode, performanceScore);
        this.primaryLanguage = primaryLanguage;
    }

    public String getPrimaryLanguage() {
        return primaryLanguage;
    }

    @Override
    public double calculatePromotionBonus(double performanceAverage) {
        if (performanceAverage >= 4.5) {
            return getBaseSalary() * 0.15;
        }
        return getBaseSalary() * 0.05;
    }
}
