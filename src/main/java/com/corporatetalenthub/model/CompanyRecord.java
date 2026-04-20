package com.corporatetalenthub.model;

// Record: less code for immutable data; a traditional class is still better when richer behavior is needed.
public record CompanyRecord(String name, String taxId, int foundationYear) {
}
