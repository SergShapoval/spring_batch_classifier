package com.example.batch.classifier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Date;

@SpringBootApplication
@EnableBatchProcessing
public class BatchclassifierApplication implements CommandLineRunner {
    private static final Logger LOGGER = LoggerFactory.getLogger(BatchclassifierApplication.class);

    private static final String PARAMETER_JOB_ID = "JobID";
    private static final String PARAMETER_DATE = "Date";
    private static final String PARAMETER_TIME = "Time";
    private JobLauncher jobLauncher;
    private Job job;

    public BatchclassifierApplication(JobLauncher jobLauncher, Job job) {
        this.jobLauncher = jobLauncher;
        this.job = job;
    }

    public static void main(String[] args) {
        SpringApplication.run(BatchclassifierApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        JobParameters jobParameters = new JobParametersBuilder()
                .addString(PARAMETER_JOB_ID, String.valueOf(System.currentTimeMillis()))
                .addDate(PARAMETER_DATE, new Date())
                .addLong(PARAMETER_TIME, System.currentTimeMillis()).toJobParameters();

        JobExecution execution = jobLauncher.run(job, jobParameters);
        LOGGER.info("Execution status: {} ", execution.getStatus());
    }
}
