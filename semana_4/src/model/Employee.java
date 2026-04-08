package model;

import contracts.Promotable;

public sealed abstract class Employee extends Person implements Promotable permits Developer, Manager {

    private final double baseSalary;

    protected Employee(int id, String name, double baseSalary) {
        super(id, name);
        this.baseSalary = baseSalary;
    }

    public double getBaseSalary() {
        return baseSalary;
    }
}
