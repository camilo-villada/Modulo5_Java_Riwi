package com.corporatetalenthub.service;

import com.corporatetalenthub.dao.EmployeeDao;
import com.corporatetalenthub.model.EmployeeListResult;
import com.corporatetalenthub.model.EmployeeOperationStatus;
import com.corporatetalenthub.model.EmployeeReportRow;
import com.corporatetalenthub.model.PersistedEmployee;
import com.corporatetalenthub.model.SalaryLevel;

import java.util.ArrayList;
import java.util.List;

public class EmployeeService {

    private final EmployeeDao employeeDao;

    public EmployeeService(EmployeeDao employeeDao) {
        this.employeeDao = employeeDao;
    }

    public EmployeeOperationStatus createEmployee(String name, String role, double salary) {
        EmployeeOperationStatus validationStatus = validateEmployeeData(0, name, role, salary, false);
        if (validationStatus != EmployeeOperationStatus.SUCCESS) {
            return validationStatus;
        }

        try {
            boolean inserted = employeeDao.insert(new PersistedEmployee(name.trim(), role.trim(), salary));
            return inserted ? EmployeeOperationStatus.SUCCESS : EmployeeOperationStatus.STORAGE_ERROR;
        } catch (RuntimeException exception) {
            return EmployeeOperationStatus.STORAGE_ERROR;
        }
    }

    public EmployeeListResult listEmployees() {
        try {
            List<PersistedEmployee> persistedEmployees = employeeDao.findAll();
            List<EmployeeReportRow> employees = new ArrayList<>(persistedEmployees.size());

            for (PersistedEmployee employee : persistedEmployees) {
                employees.add(new EmployeeReportRow(
                        employee.id(),
                        employee.name(),
                        employee.role(),
                        employee.salary(),
                        classifySalaryLevel(employee.salary())
                ));
            }

            return new EmployeeListResult(employees, true);
        } catch (RuntimeException exception) {
            return new EmployeeListResult(List.of(), false);
        }
    }

    public EmployeeOperationStatus updateEmployee(int id, String name, String role, double salary) {
        EmployeeOperationStatus validationStatus = validateEmployeeData(id, name, role, salary, true);
        if (validationStatus != EmployeeOperationStatus.SUCCESS) {
            return validationStatus;
        }

        try {
            boolean updated = employeeDao.update(new PersistedEmployee(id, name.trim(), role.trim(), salary));
            return updated ? EmployeeOperationStatus.SUCCESS : EmployeeOperationStatus.NOT_FOUND;
        } catch (RuntimeException exception) {
            return EmployeeOperationStatus.STORAGE_ERROR;
        }
    }

    public EmployeeOperationStatus deleteEmployee(int id) {
        if (id <= 0) {
            return EmployeeOperationStatus.INVALID_ID;
        }

        try {
            boolean deleted = employeeDao.delete(id);
            return deleted ? EmployeeOperationStatus.SUCCESS : EmployeeOperationStatus.NOT_FOUND;
        } catch (RuntimeException exception) {
            return EmployeeOperationStatus.STORAGE_ERROR;
        }
    }

    private EmployeeOperationStatus validateEmployeeData(int id, String name, String role, double salary, boolean validateId) {
        if (validateId && id <= 0) {
            return EmployeeOperationStatus.INVALID_ID;
        }
        if (name == null || name.isBlank()) {
            return EmployeeOperationStatus.INVALID_NAME;
        }
        if (role == null || role.isBlank()) {
            return EmployeeOperationStatus.INVALID_ROLE;
        }
        if (salary <= 0) {
            return EmployeeOperationStatus.INVALID_SALARY;
        }
        return EmployeeOperationStatus.SUCCESS;
    }

    private SalaryLevel classifySalaryLevel(double salary) {
        if (salary >= 8000) {
            return SalaryLevel.LEAD;
        }
        if (salary >= 4000) {
            return SalaryLevel.SENIOR;
        }
        return SalaryLevel.JUNIOR;
    }
}
