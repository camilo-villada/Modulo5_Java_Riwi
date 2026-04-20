package model;

public class Employee {

    private final String id;
    private final String name;
    private final String technology;
    private final String location;
    private final double salary;
    private final double performanceScore;

    public Employee(String id,
                    String name,
                    String technology,
                    String location,
                    double salary,
                    double performanceScore) {
        this.id = id;
        this.name = name;
        this.technology = technology;
        this.location = location;
        this.salary = salary;
        this.performanceScore = performanceScore;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getTechnology() {
        return technology;
    }

    public String getLocation() {
        return location;
    }

    public double getSalary() {
        return salary;
    }

    public double getPerformanceScore() {
        return performanceScore;
    }

    @Override
    public String toString() {
        return "Employee{"
                + "id='" + id + '\''
                + ", name='" + name + '\''
                + ", technology='" + technology + '\''
                + ", location='" + location + '\''
                + ", salary=" + salary
                + ", performanceScore=" + performanceScore
                + '}';
    }
}
