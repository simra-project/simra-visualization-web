package main.java.com.simra.app.csvimporter.handler;

import com.opencsv.bean.CsvToBeanBuilder;
import main.java.com.simra.app.csvimporter.model.Profile;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class ProfileDirectoryIOHandler extends DirectoryIOHandler {
    private static final Logger logger = Logger.getLogger(ProfileDirectoryIOHandler.class);

    private static final String FILEVERSIONSPLITTER = "#";

    private List<Profile> profiles = new ArrayList<>();

    public ProfileDirectoryIOHandler(Path path) {
        parseDirectory(path);
    }

    @Override
    public void parseFile(Path file) {
        try (BufferedReader reader =
                     new BufferedReader(new FileReader(String.valueOf(file)))) {
            String line = reader.readLine();
            String[] arrOfStr = line.split(FILEVERSIONSPLITTER);

            StringBuilder profileCSVwithHeader = new StringBuilder();
            while (line != null) {

                line = reader.readLine();
                if (line != null) {
                    profileCSVwithHeader.append(line);
                    profileCSVwithHeader.append("\r\n");
                }
            }

            List profileBeans = new CsvToBeanBuilder<Profile>(new StringReader(profileCSVwithHeader.toString()))
                    .withType(Profile.class).build().parse();
            if (!profileBeans.isEmpty()) {
                Profile profile = (Profile) profileBeans.get(0);
                profile.setFileId(file.getFileName().toString());
                profile.setAppVersion(arrOfStr[0]);
                profile.setFileVersion(Integer.parseInt(arrOfStr[1]));

                this.profiles.add(profile);
            }

        } catch (IOException e) {
            logger.error(e);
        }
    }

    @Override
    void writeToDB() {
        // TODO Update DB batch style (this.profiles)
    }

    @Override
    public Logger provideLogger() {
        return logger;
    }
}
