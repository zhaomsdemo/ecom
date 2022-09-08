package com.zjh.ecom.service;

import com.zjh.ecom.dao.EmployeeRepository;
import com.zjh.ecom.entity.Employee;
import com.zjh.ecom.exception.DataNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service("defaultEmployeeService")
@AllArgsConstructor
public class DefaultEmployeeServiceImpl implements EmployeeService{

    private final EmployeeRepository employeeRepository;

    @Override
    public Employee increaseEmployeeSalary(Integer employeeId, Integer salary, int delay) throws DataNotFoundException, InterruptedException {
        Optional<Employee> employeeOptional = employeeRepository.findById(employeeId);
        if (!employeeOptional.isPresent()) {
            String message = String.format("Employee %d is not found.", employeeId);
            throw new DataNotFoundException(message);
        }
        Employee employee = employeeOptional.get();
        employee.setSalary(employee.getSalary() + salary);
        if (delay > 0) {
            Thread.sleep(delay);
        }
        return employeeRepository.save(employee);
    }

    @Override
    public Employee createNewEmployee(Employee employee) {
        return employeeRepository.save(employee);
    }
}
