package com.zjh.ecom.api;

import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/job")
@AllArgsConstructor
public class JobController {

    private JobLauncher jobLauncher;

    private Job loadCustomerJob;

    @PostMapping("/load")
    @SneakyThrows
    public ResponseEntity<Void> loadCustomer() {
        JobParameters jobParameters = new JobParametersBuilder()
                .addLong("startTime",System.currentTimeMillis()).toJobParameters();

        jobLauncher.run(loadCustomerJob, jobParameters);

        return ResponseEntity.ok().build();
    }
}
