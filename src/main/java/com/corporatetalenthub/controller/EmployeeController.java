package com.corporatetalenthub.controller;

import com.corporatetalenthub.dao.EmployeeDao;
import com.corporatetalenthub.dao.JdbcEmployeeDao;
import com.corporatetalenthub.dao.EmployeeStorageInitializer;
import com.corporatetalenthub.model.EmployeeListResult;
import com.corporatetalenthub.model.EmployeeOperationStatus;
import com.corporatetalenthub.service.EmployeeService;

import java.util.List;

public class EmployeeController {

    private final EmployeeService employeeService;
    private final boolean storageReady;

    public EmployeeController() {
        this(new JdbcEmployeeDao());
    }

    public EmployeeController(EmployeeDao employeeDao) {
        boolean ready;
        try {
            new EmployeeStorageInitializer().initialize();
            ready = true;
        } catch (RuntimeException exception) {
            ready = false;
        }
        this.employeeService = new EmployeeService(employeeDao);
        this.storageReady = ready;
    }

    public EmployeeOperationStatus insertEmployee(String name, String role, double salary) {
        if (!storageReady) {
            return EmployeeOperationStatus.STORAGE_ERROR;
        }
        return employeeService.createEmployee(name, role, salary);
    }

    public EmployeeListResult listEmployees() {
        if (!storageReady) {
            return new EmployeeListResult(List.of(), false);
        }
        return employeeService.listEmployees();
    }

    public EmployeeOperationStatus updateEmployee(int id, String name, String role, double salary) {
        if (!storageReady) {
            return EmployeeOperationStatus.STORAGE_ERROR;
        }
        return employeeService.updateEmployee(id, name, role, salary);
    }

    public EmployeeOperationStatus deleteEmployee(int id) {
        if (!storageReady) {
            return EmployeeOperationStatus.STORAGE_ERROR;
        }
        return employeeService.deleteEmployee(id);
    }
}
