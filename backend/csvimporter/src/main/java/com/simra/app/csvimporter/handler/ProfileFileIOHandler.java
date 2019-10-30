package main.java.com.simra.app.csvimporter.handler;


import main.java.com.simra.app.csvimporter.model.Profile;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;

public class ProfileFileIOHandler extends FileIOHandler {
    private static final Logger logger = Logger.getLogger(ProfileFileIOHandler.class);

    private static final String FILEVERSIONSPLITTER= "#";

    public ProfileFileIOHandler(Path path) {
        super(path);
        this.fileParse();
    }

    @Override
    public void fileParse() {
        try (BufferedReader reader =
                     new BufferedReader(new FileReader(String.valueOf(this.getPath())))) {
            String line= reader.readLine();
            String[] arrOfStr = line.split(FILEVERSIONSPLITTER);

            Profile profile= new Profile();
            profile.setAppVersion(arrOfStr[0]);
            profile.setFileVersion(Integer.parseInt(arrOfStr[1]));


            StringBuilder profileCSVwithHeader = new StringBuilder();
            while (line != null) {

                line = reader.readLine();
                if(line != null){
                    profileCSVwithHeader.append("\n");
                    profileCSVwithHeader.append(line);
                }

            }
            logger.info(profileCSVwithHeader.toString());

        }catch (IOException e){
            logger.error(e);
        }
    }
}
