package main.java.com.simra.app.csvimporter.services;

import main.java.com.simra.app.csvimporter.handler.RideFileIOHandler;
import org.apache.log4j.Logger;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;

public class RideImportTask implements Runnable {

    private static final Logger logger = Logger.getLogger(RideImportTask.class);
    private String filePath;
    private Float minAccuracy;
    private Double rdpEpsilon;

    public RideImportTask(String filePath) {
        this.filePath = filePath;
    }

    public RideImportTask(String filePath, Float minAccuracy, Double rdpEpsilon) {
        this.filePath = filePath;
        this.minAccuracy=minAccuracy;
        this.rdpEpsilon=rdpEpsilon;
    }

    public String getFilePath() {
        return filePath;
    }

    public void run() {
        Path path = Paths.get(this.filePath);
        new RideFileIOHandler(path, minAccuracy, rdpEpsilon);
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


    }
}
