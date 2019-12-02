package com.simra.app.csvimporter;

import com.simra.app.csvimporter.controller.MonitorService;
import com.simra.app.csvimporter.service.StatisticsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class CsvimporterApplication implements CommandLineRunner {
    private static final Logger logger = LoggerFactory.getLogger(CsvimporterApplication.class);

    @Autowired
    private MonitorService monitorService;


    public static void main(String[] args) {
        if (args[0].equals("--statistics")) {
            (new StatisticsService()).calculateStatistics();
            return;
        }

        SpringApplication.run(CsvimporterApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        monitorService.startRecursiveWatcher();
    }

}
