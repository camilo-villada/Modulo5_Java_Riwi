package com.corporatetalenthub.model;

// Legacy Java 8/11: an abstract class kept inheritance open.
// Java 17/21: a sealed class restricts extension to explicit types, protecting domain rules.
public sealed abstract class Person permits Employee, ExternalConsultant {

    private final int id;
    private final String name;

    protected Person(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
