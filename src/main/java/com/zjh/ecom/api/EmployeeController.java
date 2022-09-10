package com.zjh.ecom.api;

import com.zjh.ecom.entity.Employee;
import com.zjh.ecom.exception.DataNotFoundException;
import com.zjh.ecom.service.EmployeeService;
import com.zjh.ecom.service.MultipleThreadingRunner;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

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
    public ResponseEntity<Employee> changeSalary(@PathVariable Integer employeeId, @PathVariable Integer salary, @RequestHeader int delay) {
        try {
            Employee employee = employeeService.increaseEmployeeSalary(employeeId, salary, delay);
            return ResponseEntity.ok(employee);
        } catch (DataNotFoundException e) {
            return (ResponseEntity<Employee>) ResponseEntity.notFound();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @PostMapping("/simulate")
    public ResponseEntity<Void> simulate(@RequestHeader int cycle) {
        ThreadPoolExecutor executor = new ThreadPoolExecutor(2000, 5000,
                2, TimeUnit.MINUTES, new ArrayBlockingQueue<Runnable>(5000));
        CountDownLatch latch = new CountDownLatch(1);
        for (int i = 0; i < cycle; i++) {
            MultipleThreadingRunner runner = new MultipleThreadingRunner(employeeService, latch);
            executor.execute(runner);
        }
        latch.countDown();
        return ResponseEntity.ok().build();
    }
}
