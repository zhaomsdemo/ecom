package com.zjh.ecom.service;

import com.zjh.ecom.exception.DataNotFoundException;

import java.util.Random;
import java.util.concurrent.CountDownLatch;

public class MultipleThreadingRunner implements Runnable{

    private EmployeeService employeeService;
    private CountDownLatch latch;

    private int employeeId;

    public MultipleThreadingRunner(EmployeeService employeeService, CountDownLatch latch, int employeeId) {
        this.employeeService = employeeService;
        this.latch = latch;
        this.employeeId = employeeId;
    }

    @Override
    public void run() {
        Random random = new Random();
        int salary = random.nextInt(2000) - 1000;
        int delay = random.nextInt(10) * 1000;
        try {
            latch.await();
            employeeService.increaseEmployeeSalary(employeeId, salary, delay);
        } catch (DataNotFoundException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
