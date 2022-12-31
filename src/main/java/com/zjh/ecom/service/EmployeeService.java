package com.zjh.ecom.service;

import com.zjh.ecom.entity.Employee;
import com.zjh.ecom.exception.DataNotFoundException;

public interface EmployeeService {

    Employee increaseEmployeeSalary(Integer employeeId , Integer salary, int delay) throws DataNotFoundException, InterruptedException;

    Employee createNewEmployee(Employee employee);

    Employee getEmployeeById(Integer employeeId);
}
