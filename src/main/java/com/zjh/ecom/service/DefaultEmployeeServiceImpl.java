package com.zjh.ecom.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zjh.ecom.dao.EmployeeRepository;
import com.zjh.ecom.entity.Employee;
import com.zjh.ecom.exception.DataNotFoundException;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import javax.validation.constraints.NotNull;
import java.util.Optional;

@Service("defaultEmployeeService")
@Slf4j
@RequiredArgsConstructor
public class DefaultEmployeeServiceImpl implements EmployeeService{

    @NotNull
    private EmployeeRepository employeeRepository;
    @NotNull
    private KafkaTemplate kafkaTemplate;
    @NotNull
    private ObjectMapper objectMapper;
    @Value("${spring.kafka.topics.employee}")
    private String employeeTopic;

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
    public Employee createNewEmployee(Employee employee) throws JsonProcessingException {
        Employee newEmployee =  employeeRepository.save(employee);
        kafkaTemplate.send(employeeTopic, objectMapper.writeValueAsString(newEmployee));
        return newEmployee;
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
