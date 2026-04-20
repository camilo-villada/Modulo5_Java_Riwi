package report;

// Legacy Java 8/11: this immutable data shape required a full POJO with constructor,
// getters, and toString implementation.
// Java 17/21: a record provides the same intent with less boilerplate and immutable state.
public record PerformanceReport(int employeeId, double average, String feedback) {
}
