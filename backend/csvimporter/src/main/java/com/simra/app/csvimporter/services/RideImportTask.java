package main.java.com.simra.app.csvimporter.services;

import main.java.com.simra.app.csvimporter.handler.RideDirectoryIOHandler;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;

/**
 * The type Ride import task.
 */
public class RideImportTask implements Runnable {

    private String filePath;
    private Float minAccuracy;
    private Double rdpEpsilon;

    /**
     * Instantiates a new Ride import task.
     *
     * @param filePath    the file path
     * @param minAccuracy the min accuracy
     * @param rdpEpsilon  the rdp epsilon
     */
    public RideImportTask(String filePath, Float minAccuracy, Double rdpEpsilon) {
        this.filePath = filePath;
        this.minAccuracy=minAccuracy;
        this.rdpEpsilon=rdpEpsilon;
    }

    /**
     * Gets file path.
     *
     * @return the file path
     */
    public String getFilePath() {
        return filePath;
    }

    public void run() {
        Path path = Paths.get(this.filePath);
        new RideDirectoryIOHandler(path, minAccuracy, rdpEpsilon);
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


    }
}
