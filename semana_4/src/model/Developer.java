package model;

public final class Developer extends Employee {

    private final String primaryLanguage;

    public Developer(int id, String name, double baseSalary, String primaryLanguage) {
        super(id, name, baseSalary);
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
