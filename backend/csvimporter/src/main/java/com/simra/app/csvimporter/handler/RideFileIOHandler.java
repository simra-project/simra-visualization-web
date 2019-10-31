package main.java.com.simra.app.csvimporter.handler;

import com.opencsv.bean.CsvToBeanBuilder;
import main.java.com.simra.app.csvimporter.model.IncidentCSV;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Path;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RideFileIOHandler extends FileIOHandler {
    private static final Logger logger = Logger.getLogger(RideFileIOHandler.class);

    private static final String FILEVERSIONSPLITTER = "#";


    public RideFileIOHandler(Path path) {
        super(path);
        this.fileParse();
    }

    @Override
    public void fileParse() {
        // parses ride file, as its has different structure.
        try (BufferedReader reader =
                     new BufferedReader(new FileReader(String.valueOf(this.getPath())))) {
            StringBuilder incidentContent = new StringBuilder();
            StringBuilder rideContent = new StringBuilder();

            boolean switchStream = false;
            String line = reader.readLine();

            while (line != null) {
                if (line.contains("=========================")) {
                    switchStream = true;
                }
                if (switchStream) {
                    rideContent.append(line);
                    rideContent.append("\r\n ");
                } else {

                    incidentContent.append(line);
                    incidentContent.append("\r\n");
                }
                line = reader.readLine();
            }
            if (incidentContent.length() > 0) {
                this.incidentParse(incidentContent);
            }
            if (rideContent.length() > 0) {
                this.rideParse(rideContent);
            }

        } catch (IOException e) {
            logger.error(e);
        }
    }

    private void incidentParse(StringBuilder incidentContent) {

        try (Scanner scanner = new Scanner(incidentContent.toString())) {
            String firstLine = scanner.nextLine();
            StringBuilder incidentCSVwithHeader = new StringBuilder();
            while (scanner.hasNextLine()) {
                String currentLine = scanner.nextLine();
                if (!currentLine.isEmpty()) {
                    String[] vals = currentLine.split(",", -1);

                    Stream<String> recordStream = Stream.of(vals);
                    String newLine = recordStream.map(o -> o.isEmpty() ? "0" : o).collect(Collectors.joining(","));
                    incidentCSVwithHeader.append(newLine).append("\r\n");
                }
            }
            logger.info(incidentCSVwithHeader);
            List incidentBeans = new CsvToBeanBuilder<IncidentCSV>(new StringReader(incidentCSVwithHeader.toString()))
                    .withType(IncidentCSV.class).build().parse();
            logger.info(incidentBeans.size());

        } catch (Exception e) {
            logger.error(e);
        }
    }

    private void rideParse(StringBuilder rideContent) {
        logger.info("TODO this one");
        logger.info(rideContent);

    }

    private void filePathAppVersion() {
        logger.info("TODO parse file path");
        logger.info(FILEVERSIONSPLITTER);
    }

}
