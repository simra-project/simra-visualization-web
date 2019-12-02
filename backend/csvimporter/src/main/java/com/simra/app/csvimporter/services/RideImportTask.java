package main.java.com.simra.app.csvimporter.services;

import main.java.com.simra.app.csvimporter.handler.RideFileIOHandler;

import java.nio.file.Path;

/**
 * The type Ride import task.
 */
public class RideImportTask implements Runnable {

    private Path filePath;
    private String region;
    private Float minAccuracy;
    private Double rdpEpsilon;

    /**
     * Instantiates a new Ride import task.
     *
     * @param filePath    the file path
     * @param region      the region of data origin
     * @param minAccuracy the min accuracy
     * @param rdpEpsilon  the rdp epsilon
     */
    public RideImportTask(Path filePath, String region, Float minAccuracy, Double rdpEpsilon) {
        this.filePath = filePath;
        this.region = region;
        this.minAccuracy = minAccuracy;
        this.rdpEpsilon = rdpEpsilon;
    }

    /**
     * Gets file path.
     *
     * @return the file path
     */
    public Path getFilePath() {
        return filePath;
    }

    public void run() {
        RideFileIOHandler rideFileIOHandler = new RideFileIOHandler(filePath, region, minAccuracy, rdpEpsilon);
        rideFileIOHandler.parseFile();
    }
}
