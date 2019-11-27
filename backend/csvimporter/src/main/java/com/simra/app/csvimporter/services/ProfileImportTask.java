package main.java.com.simra.app.csvimporter.services;

import main.java.com.simra.app.csvimporter.handler.ProfileDirectoryIOHandler;
import org.apache.log4j.Logger;

import java.nio.file.Path;
import java.util.concurrent.TimeUnit;

/**
 * The type Profile import task.
 */
public class ProfileImportTask implements Runnable {

    private static final Logger logger = Logger.getLogger(ProfileImportTask.class);
    private Path filePath;


    /**
     * Instantiates a new Profile import task.
     *
     * @param filePath the file path
     */
    public ProfileImportTask(Path filePath) {
        this.filePath = filePath;
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
        new ProfileDirectoryIOHandler(filePath);
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


    }
}
