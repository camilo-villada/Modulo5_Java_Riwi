package model;

public final class Manager extends Employee {

    private final double monthlyBudget;

    public Manager(int id, String name, double baseSalary, double monthlyBudget) {
        super(id, name, baseSalary);
        this.monthlyBudget = monthlyBudget;
    }

    public double getMonthlyBudget() {
        return monthlyBudget;
    }

    @Override
    public double calculatePromotionBonus(double performanceAverage) {
        double bonusBase;
        if (performanceAverage >= 4.5) {
            bonusBase = getBaseSalary() * 0.20;
        } else {
            bonusBase = getBaseSalary() * 0.07;
        }
        return bonusBase + (monthlyBudget * 0.01);
    }
}
