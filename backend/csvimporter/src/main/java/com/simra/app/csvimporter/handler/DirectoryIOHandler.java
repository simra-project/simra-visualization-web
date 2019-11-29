package main.java.com.simra.app.csvimporter.handler;

import main.java.com.simra.app.csvimporter.services.DBService;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

public abstract class DirectoryIOHandler {

    static DBService dbService;

    DirectoryIOHandler() {
        dbService = DBService.getInstance();
    }

    boolean canOpenDirectory(Path directory) {
        return directory.toFile().isDirectory();
    }

    void parseDirectory(Path directory) {
        /*
         * parses ride dir, as its has different structure.
         */
        if (!canOpenDirectory(directory)) throw new IllegalArgumentException("Directoy is innvalid");
        long begin = System.currentTimeMillis();
        try (Stream<Path> filePaths = Files.walk(directory)) {
            filePaths
                    .filter(Files::isRegularFile)
                    .forEach(this::parseFile);

            writeToDB();
        } catch (IOException e) {
        }
        long end = System.currentTimeMillis();
        long dt = end - begin;
        System.out.println("Import of CSV took " + dt / 1000 + " seconds.");
    }

    abstract void parseFile(Path file);

    abstract void writeToDB();

}
