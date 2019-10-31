package main.java.com.simra.app.csvimporter.handler;

import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBeanBuilder;
import main.java.com.simra.app.csvimporter.model.IncidentCSV;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Scanner;

public class RideFileIOHandler extends FileIOHandler {
    private static final Logger logger = Logger.getLogger(RideFileIOHandler.class);

    private static final String FILEVERSIONSPLITTER = "#";


    public RideFileIOHandler(Path path) {
        super(path);
        this.fileParse();
    }

    @Override
    public void fileParse() {
        /*
         * parses ride file, as its has different structure.
         */
        try (BufferedReader reader = Files.newBufferedReader(this.getPath(), StandardCharsets.UTF_8)){
            StringBuilder incidentContent = new StringBuilder();
            StringBuilder rideContent = new StringBuilder();

            boolean switchStream = false;
            String line = reader.readLine();

            while (line != null) {
                if (line.contains("=========================") || line.contains("===================")) {
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
                this.incidentParse(incidentContent, this.getPath());
            }
            if (rideContent.length() > 0) {
                this.rideParse();
            }

        } catch (IOException e ) {
            logger.error(e);
        }
    }

    private void incidentParse(StringBuilder incidentContent, Path path) {

        try (Scanner scanner = new Scanner(incidentContent.toString())) {
            String firstLine = scanner.nextLine();
            String[] arrOfStr = firstLine.split(FILEVERSIONSPLITTER);

            StringBuilder incidentCSVwithHeader = new StringBuilder();
            while (scanner.hasNextLine()) {
                String currentLine = scanner.nextLine();
                if (!currentLine.isEmpty()) {
                    incidentCSVwithHeader.append(currentLine).append("\r\n");
                }
            }

            ColumnPositionMappingStrategy<IncidentCSV> strategy = new ColumnPositionMappingStrategy<>();
            strategy.setType(IncidentCSV.class);
            String[] memberFieldsToBindTo = {"key","lat","lon","ts","bike","childCheckBox","trailerCheckBox","pLoc","incident","i1","i2","i3","i4","i5","i6","i7","i8","i9","scary","desc","i10"};
            strategy.setColumnMapping(memberFieldsToBindTo);

            List<IncidentCSV> incidentBeans = new CsvToBeanBuilder<IncidentCSV>(new StringReader(incidentCSVwithHeader.toString()))
                    .withMappingStrategy(strategy)
                    .withSkipLines(1)
                    .withIgnoreLeadingWhiteSpace(true)
                    .build().parse();
            incidentBeans.forEach(item -> {
                item.setFileId(path.getFileName().toString());
                item.setAppVersion(arrOfStr[0]);
                item.setFileVersion(Integer.parseInt(arrOfStr[1]));
                logger.info(item.getFileId());
                logger.info(item.getFileVersion());
                logger.info(item.getAppVersion());

            });
        } catch (Exception e) {
            logger.error(e);
        }
    }

    private void rideParse() {

        // ride parser left
    }

}
