package main.java.com.simra.app.csvimporter.handler;


import com.opencsv.bean.CsvToBeanBuilder;
import main.java.com.simra.app.csvimporter.model.Profile;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Path;
import java.util.List;

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

            StringBuilder profileCSVwithHeader = new StringBuilder();
            while (line != null) {

                line = reader.readLine();
                if(line != null){
                    profileCSVwithHeader.append(line);
                    profileCSVwithHeader.append("\r\n");
                }

            }

            List profileBeans = new CsvToBeanBuilder<Profile>(new StringReader(profileCSVwithHeader.toString()))
                    .withType(Profile.class).build().parse();
            if (!profileBeans.isEmpty()) {
                Profile profile = (Profile) profileBeans.get(0);
                profile.setAppVersion(arrOfStr[0]);
                profile.setFileVersion(Integer.parseInt(arrOfStr[1]));
                logger.info(profile.toString());
            }


        }catch (IOException e){
            logger.error(e);
        }
    }
}
