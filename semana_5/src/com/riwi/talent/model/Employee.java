package com.riwi.talent.model;

/*
 * Record in Java 17:
 * - Reduces boilerplate compared to a traditional POJO.
 * - Generates constructor, getters, equals, hashCode and toString automatically.
 * - Its immutable nature helps keep data safer and more predictable.
 */
public record Employee(int id, String name, String role, double salary) {

    public Employee(String name, String role, double salary) {
        this(0, name, role, salary);
    }
}
