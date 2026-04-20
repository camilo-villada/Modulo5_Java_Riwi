package com.riwi.talent.model;

import java.util.List;

public interface EmployeeDAO {

    boolean insert(Employee employee);

    List<EmployeeDTO> findAll();

    boolean update(Employee employee);

    boolean delete(int id);
}
