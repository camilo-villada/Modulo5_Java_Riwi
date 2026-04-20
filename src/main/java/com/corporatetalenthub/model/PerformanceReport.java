package com.corporatetalenthub.model;

// Legacy Java 8/11: this immutable data shape required a full POJO.
// Java 17/21: a record represents the same intent with less boilerplate.
public record PerformanceReport(int employeeId, double average, PerformanceFeedback feedback) {
}
