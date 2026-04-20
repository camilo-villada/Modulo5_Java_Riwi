package model;

// Legacy Java 8/11: "abstract class Person" keeps inheritance open to any subclass.
// Java 17+: sealed class restricts extension to explicit types, protecting domain rules
// and making API boundaries safer and easier to maintain.
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
