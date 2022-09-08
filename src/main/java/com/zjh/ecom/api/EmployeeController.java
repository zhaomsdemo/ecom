package com.zjh.ecom.api;

import com.zjh.ecom.entity.Employee;
import com.zjh.ecom.exception.DataNotFoundException;
import com.zjh.ecom.service.EmployeeService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/employee")
@AllArgsConstructor
public class EmployeeController {

    private final EmployeeService employeeService;

    @PostMapping
    public ResponseEntity<Employee> register(@RequestBody Employee employee) {
        return ResponseEntity.ok(employeeService.createNewEmployee(employee));
    }

    @PutMapping("/{employeeId}/salary/{salary}")
    public ResponseEntity<Employee> changeSalary(@PathVariable Integer employeeId,@PathVariable Integer salary,@RequestHeader int delay) {
        try {
            Employee employee = employeeService.increaseEmployeeSalary(employeeId, salary, delay);
            return ResponseEntity.ok(employee);
        } catch (DataNotFoundException e) {
            return (ResponseEntity<Employee>) ResponseEntity.notFound();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
