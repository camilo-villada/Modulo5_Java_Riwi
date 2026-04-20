package com.corporatetalenthub.dao;

import com.corporatetalenthub.model.PersistedEmployee;

import java.util.List;

public interface EmployeeDao {

    boolean insert(PersistedEmployee employee);

    List<PersistedEmployee> findAll();

    boolean update(PersistedEmployee employee);

    boolean delete(int id);
}
