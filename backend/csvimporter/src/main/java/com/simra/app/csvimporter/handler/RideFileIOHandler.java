package main.java.com.simra.app.csvimporter.handler;

import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBeanBuilder;
import main.java.com.simra.app.csvimporter.filter.RamerDouglasPeuckerFilter;
import main.java.com.simra.app.csvimporter.filter.RideFilter;
import main.java.com.simra.app.csvimporter.dbservice.DBService;
import main.java.com.simra.app.csvimporter.mapmatching.MapMatchingService;
import main.java.com.simra.app.csvimporter.model.IncidentCSV;
import main.java.com.simra.app.csvimporter.model.Ride;
import main.java.com.simra.app.csvimporter.model.RideCSV;
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
    private Ride ride;

    private RideFilter rideFilter;
    private MapMatchingService mapMatchingService = new MapMatchingService();

    private static DBService dbService;

    public RideFileIOHandler(Path path, Float minAccuracy, Double rdpEpsilon) {
        super(path);
        dbService = new DBService();
        dbService.DbRideConnect();
        this.ride = new Ride();
        this.rideFilter = new RideFilter(minAccuracy, rdpEpsilon);
        this.fileParse();
    }

    @Override
    public void fileParse() {
        /*
         * parses ride file, as its has different structure.
         */
        try (BufferedReader reader = Files.newBufferedReader(this.getPath(), StandardCharsets.UTF_8)) {
            StringBuilder incidentContent = new StringBuilder();
            StringBuilder rideContent = new StringBuilder();

            boolean switchStream = false;
            String line = reader.readLine();

            while (line != null) {
                if (line.contains("==")) {
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
                this.rideParse(rideContent, this.getPath());
            }

            if(!this.ride.getRideBeans().isEmpty()) {
                this.ride.getRideBeans().forEach(item -> {
                    logger.info(item.toString());
                });
                List<RideCSV> optimisedRideBeans = rideFilter.filterRide(this.ride);
                optimisedRideBeans.forEach(item-> logger.info(item.toString()));
                this.ride.setRideBeans(optimisedRideBeans);
            }

            List snappedRideBeans = mapMatchingService.matchToMap(ride.getRideBeans());
            //TODO do sth with snappedRideBeans
            dbService.getCollection().insertOne(this.ride.toDocumentObject());

        } catch (IOException e) {
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
            String[] memberFieldsToBindTo = {"key", "lat", "lon", "ts", "bike", "childCheckBox", "trailerCheckBox", "pLoc", "incident", "i1", "i2", "i3", "i4", "i5", "i6", "i7", "i8", "i9", "scary", "desc", "i10"};
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
            });
            this.ride.setIncidents(incidentBeans);
        } catch (Exception e) {
            logger.error(e);
        }
    }

    private void rideParse(StringBuilder rideContent, Path path) {
        try (Scanner scanner = new Scanner(rideContent.toString())) {
            scanner.nextLine();
            String fileAppLine = scanner.nextLine();
            String[] arrOfStr = fileAppLine.split(FILEVERSIONSPLITTER);

            StringBuilder rideCSVwithHeader = new StringBuilder();
            while (scanner.hasNextLine()) {
                String currentLine = scanner.nextLine();
                /*
                 * add ride information only if it has location data.
                 */
                if (currentLine.trim().length() > 0 && !currentLine.isEmpty() && !currentLine.trim().startsWith(",")) {
                    rideCSVwithHeader.append(currentLine.trim()).append("\r\n");
                }
            }


            List<RideCSV> rideBeans = new CsvToBeanBuilder<RideCSV>(new StringReader(rideCSVwithHeader.toString().trim()))
                    .withType(RideCSV.class).build().parse();

            rideBeans.forEach(item -> {
                item.setFileId(path.getFileName().toString());
                item.setAppVersion(arrOfStr[0]);
                item.setFileVersion(Integer.parseInt(arrOfStr[1]));
            });
            this.ride.setRideBeans(rideBeans);

        } catch (Exception e) {
            logger.error(e);
        }

    }

}
