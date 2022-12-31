package com.zjh.ecom.service;

import com.zjh.ecom.dao.EmployeeRepository;
import com.zjh.ecom.entity.Employee;
import com.zjh.ecom.exception.DataNotFoundException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;

@Service("defaultEmployeeService")
@AllArgsConstructor
@Slf4j
public class DefaultEmployeeServiceImpl implements EmployeeService{

    private final EmployeeRepository employeeRepository;

    @Override
    @Transactional
    public Employee increaseEmployeeSalary(Integer employeeId, Integer salary, int delay) throws DataNotFoundException, InterruptedException {
        log.info("increaseEmployeeSalary {} salary {} started", employeeId, salary);
        Optional<Employee> employeeOptional = employeeRepository.findById(employeeId);
        if (!employeeOptional.isPresent()) {
            String message = String.format("Employee %d is not found.", employeeId);
            throw new DataNotFoundException(message);
        }
        Employee employee = employeeOptional.get();
        if (delay > 0) {
            Thread.sleep(delay);
        }
        employee.setSalary(employee.getSalary() + salary);
        employee = employeeRepository.save(employee);
        log.info("increaseEmployeeSalary {} salary {} completed", employeeId, salary);
        return employee;
    }

    @Override
    public Employee createNewEmployee(Employee employee) {
        return employeeRepository.save(employee);
    }

    @Override
    public Employee getEmployeeById(Integer employeeId) {
        Optional<Employee> optEmployee = employeeRepository.findById(employeeId);
        if (optEmployee.isPresent()) {
            return optEmployee.get();
        }
        String message = String.format("Employee Id %s Not Found", employeeId);
        throw new DataNotFoundException(message);
    }
}
