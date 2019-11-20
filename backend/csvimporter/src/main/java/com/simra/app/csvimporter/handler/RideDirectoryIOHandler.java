package main.java.com.simra.app.csvimporter.handler;

import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBeanBuilder;
import main.java.com.simra.app.csvimporter.filter.RideFilter;
import main.java.com.simra.app.csvimporter.mapmatching.MapMatchingService;
import main.java.com.simra.app.csvimporter.model.IncidentCSV;
import main.java.com.simra.app.csvimporter.model.Ride;
import main.java.com.simra.app.csvimporter.model.RideCSV;
import main.java.com.simra.app.csvimporter.services.ConfigService;
import org.apache.log4j.Logger;
import org.bson.Document;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class RideDirectoryIOHandler extends DirectoryIOHandler {
    private static final Logger logger = Logger.getLogger(RideDirectoryIOHandler.class);

    private static final String FILEVERSIONSPLITTER = "#";
    private List<Ride> rides = new ArrayList<>();

    private RideFilter rideFilter;
    private MapMatchingService mapMatchingService = new MapMatchingService();

    public RideDirectoryIOHandler(Path path, Float minAccuracy, Double rdpEpsilon) {
        dbService.DbRideConnect();
        this.rideFilter = new RideFilter(minAccuracy, rdpEpsilon);
        parseDirectory(path);
    }

    @Override
    public void parseFile(Path file) {
        try (BufferedReader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
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
            Ride ride = new Ride();
            if (incidentContent.length() > 0) {
                this.incidentParse(ride, incidentContent, file);
            }
            if (rideContent.length() > 0) {
                this.rideParse(ride, rideContent, file);
            }

            if (ride.getRideBeans().isEmpty()) {
                if (Boolean.getBoolean(ConfigService.config.getProperty("debug"))) {
                    ride.getRideBeans().forEach(item -> {
                        logger.info(item.toString());
                    });
                }

                List<RideCSV> optimisedRideBeans = rideFilter.filterRide(ride);
                if (Boolean.getBoolean(ConfigService.config.getProperty("debug"))) {
                    optimisedRideBeans.forEach(item -> logger.info(item.toString()));
                }
                ride.setRideBeans(optimisedRideBeans);
            }

            List mapMatchedRideBeans = mapMatchingService.matchToMap(ride.getRideBeans());
            ride.setMapMatchedRideBeans(mapMatchedRideBeans);

            this.rides.add(ride);

        } catch (IOException e) {
            logger.error(e);
        }
    }

    private void incidentParse(Ride ride, StringBuilder incidentContent, Path path) {

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
            ride.setIncidents(incidentBeans);
        } catch (Exception e) {
            logger.error(e);
        }
    }

    private void rideParse(Ride ride, StringBuilder rideContent, Path path) {
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
            ride.setRideBeans(rideBeans);

        } catch (Exception e) {
            logger.error(e);
        }
    }

    @Override
    void writeToDB() {
        dbService.getCollection().insertMany(this.rides.stream().map(Ride::toDocumentObject).collect(Collectors.toList()));
        List<Document> incidents = this.rides.stream().flatMap(it -> it.incidentsDocuments().stream()).collect(Collectors.toList());
        if(!incidents.isEmpty()) {
            dbService.getCollection().insertMany(incidents);
        }

        dbService.DBMapMatchedRideConnect();
        dbService.getCollection().insertMany(this.rides.stream().map(Ride::toDocumentObject).collect(Collectors.toList()));
    }

    @Override
    public Logger provideLogger() {
        return logger;
    }
}
