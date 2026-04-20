package com.corporatetalenthub.model;

import java.util.List;

public record EmployeeListResult(List<EmployeeReportRow> employees, boolean storageAvailable) {
}
