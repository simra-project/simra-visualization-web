package main.java.com.simra.app.csvimporter.handler;


import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;

public class ProfileFileIOHandler extends FileIOHandler {
    private static final Logger logger = Logger.getLogger(ProfileFileIOHandler.class);

    public ProfileFileIOHandler(Path path) {
        super(path);
    }

    @Override
    public void fileParse() {
        try (BufferedReader reader =
                     new BufferedReader(new FileReader(String.valueOf(this.getPath())))) {
            String line= reader.readLine();
            while (line != null) {

                line = reader.readLine();
            }

        }catch (IOException e){
            logger.error(e);
        }
    }
}
