package main.java.com.simra.app.csvimporter.services;

import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class ThreadController {

    private static final Logger logger = Logger.getLogger(ThreadController.class);


    private ArrayList<String> files;
    private ThreadPoolExecutor executor;
    private String type;
    private Float minAccuracy;
    private Double rdpEpsilon;


    public ThreadController(ArrayList<String> results, String type, Float minAccuracy, Double rdpEpsilon) {
        this.files= results;
        this.executor= (ThreadPoolExecutor) Executors.newFixedThreadPool(10);
        this.type=type;
        this.minAccuracy=minAccuracy;
        this.rdpEpsilon=rdpEpsilon;
    }

    public void executeFileRead(){

        this.files.stream().forEach(filePath->{
            if(this.type.contains("r")){
                RideImportTask rideImportTask = new RideImportTask(filePath, this.minAccuracy, this.rdpEpsilon);
                logger.info("Ride Task Created : " + rideImportTask.getFilePath());
                this.executor.execute(rideImportTask);
            }
            if(this.type.contains("p")){
                ProfileImportTask profileImportTask = new ProfileImportTask(filePath);
                logger.info("Ride Task Created : " + profileImportTask.getFilePath());
                this.executor.execute(profileImportTask);
            }

        });

        this.executor.shutdown();
        logger.info("File imports done");

    }
}
