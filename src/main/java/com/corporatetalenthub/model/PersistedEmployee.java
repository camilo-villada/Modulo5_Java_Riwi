package com.corporatetalenthub.model;

/*
 * Record in Java 17/21:
 * - Reduces boilerplate compared to a traditional POJO.
 * - Generates constructor, accessors, equals, hashCode and toString automatically.
 * - The immutable shape makes CRUD data safer and more predictable.
 */
public record PersistedEmployee(int id, String name, String role, double salary) {

    public PersistedEmployee(String name, String role, double salary) {
        this(0, name, role, salary);
    }
}
