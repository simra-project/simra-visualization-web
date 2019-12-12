package com.simra.app.csvimporter.service;

import com.mongodb.client.model.geojson.LineString;
import com.mongodb.client.model.geojson.Position;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBeanBuilder;
import com.simra.app.csvimporter.controller.LegRepository;
import com.simra.app.csvimporter.controller.RideRepository;
import com.simra.app.csvimporter.filter.MapMatchingService;
import com.simra.app.csvimporter.filter.RideSmoother;
import com.simra.app.csvimporter.model.LegEntity;
import com.simra.app.csvimporter.model.LegPropertyEntity;
import com.simra.app.csvimporter.model.RideCSV;
import com.simra.app.csvimporter.model.RideEntity;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.geo.Point;
import org.springframework.data.mongodb.core.geo.GeoJsonLineString;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class RideParserThreaded implements Runnable {
    private static final Logger LOG = LoggerFactory.getLogger(RideParserThreaded.class);


    private String fileName;

    private String csvString;

    private RideRepository rideRepository;

    private LegRepository legRepository;

    private RideSmoother rideSmoother;

    private MapMatchingService mapMatchingService;

    private String region;

    private Integer minRideDistance;

    private Integer minRideDuration;

    private Integer maxRideAverageSpeed;

    private Integer minDistanceToCoverByUserIn5Min;

    public RideParserThreaded(
            String fileName,
            RideRepository rideRepository,
            LegRepository legRepository,
            Float minAccuracy,
            double rdpEpsilon,
            MapMatchingService mapMatchingService,
            String csvString,
            String region,
            Integer minRideDistance,
            Integer minRideDuration,
            Integer maxRideAverageSpeed,
            Integer minDistanceToCoverByUserIn5Min) {

        this.fileName = fileName;
        this.csvString = csvString;
        this.rideRepository = rideRepository;
        this.legRepository = legRepository;
        this.rideSmoother = new RideSmoother(minAccuracy, rdpEpsilon);
        this.mapMatchingService = mapMatchingService;
        this.region = region;
        this.minRideDistance = minRideDistance;
        this.minRideDuration = minRideDuration * 60 * 1000; // minutes to millis
        this.maxRideAverageSpeed = maxRideAverageSpeed;
        this.minDistanceToCoverByUserIn5Min = minDistanceToCoverByUserIn5Min;
    }


    @Override
    public void run() {
        LOG.info("Ride parser running {}", this.fileName);
        try (BufferedReader reader = new BufferedReader(new StringReader(this.csvString))) {
            String line = reader.readLine();
            String[] arrOfStr = line.split("#");

            while (line != null) {
                line = reader.readLine();
                if (line.contains("==")) {
                    break;
                }
            }
            StringBuilder rideCSVwithHeader = new StringBuilder();
            String currentLine = reader.readLine();
            while (currentLine != null) {
                currentLine = reader.readLine();
                /*
                 * add ride information only if it has location data.
                 */
                if (currentLine == null) {
                    break;
                }
                if (currentLine.trim().length() > 0 && !currentLine.isEmpty() && !currentLine.trim().startsWith(",")) {
                    rideCSVwithHeader.append(currentLine.trim()).append("\r\n");
                }
            }
            ColumnPositionMappingStrategy<RideEntity> strategy = new ColumnPositionMappingStrategy<>();
            strategy.setType(RideEntity.class);
            String[] memberFieldsToBindTo = {"lat", "lon", "X", "Y", "Z", "timeStamp", "acc", "a", "b", "c"};
            strategy.setColumnMapping(memberFieldsToBindTo);

            List<RideCSV> rideBeans = new CsvToBeanBuilder<RideCSV>(new StringReader(rideCSVwithHeader.toString().trim()))
                    .withMappingStrategy(strategy)
                    .withSkipLines(1)
                    .withIgnoreLeadingWhiteSpace(true)
                    .build().parse();

            /*
             * ALl filters before DB Entity must chain here.
             */
            // Acc & RDP Filter
            List<RideCSV> optimisedRideBeans = this.rideSmoother.smoothRide(rideBeans);
            // Map Matching
            List<RideCSV> mapMatchedRideBeans = mapMatchingService.matchToMap(optimisedRideBeans);

            Float routeDistance = mapMatchingService.getCurrentRouteDistance();
            Long routeDuration = mapMatchingService.getCurrentRouteDuration();

            // filter short Distance Rides
            if (routeDistance < minRideDistance) {
                LOG.info(fileName + " filtered due to routeDistance = " + routeDistance + "m");
                return;
            }

            // filter short Duration Rides
            if (routeDuration < minRideDuration) {
                LOG.info(fileName + " filtered due to routeDuration = " + (routeDuration / 60000) + "min");
                return;
            }

            // filter high average speed
            double averageSpeed = Utils.calcAverageSpeed(routeDistance, routeDuration);
            if (averageSpeed > maxRideAverageSpeed) {
                LOG.info(fileName + " filtered due to averageSpeed = " + averageSpeed + "km/h");
                return;
            }

            // filter rides that user did not stop
            if (isUserForgotToStopRecording(optimisedRideBeans)) {
                LOG.info(fileName + " filtered due to User forgot to stop recording");
                //return;
            }

            /*
             * All filters related to csv parsed data must end before this
             */

            RideEntity rideEntity = getRideEntity(arrOfStr, optimisedRideBeans);
            rideEntity.setRegion(region);
            /*
             * needed modified entity properties must insert here.
             */
            rideEntity.setMapMatchedRideBeans(mapMatchedRideBeans);
            rideEntity.setDistance(mapMatchingService.getCurrentRouteDistance());
            rideEntity.setDuration(mapMatchingService.getCurrentRouteDuration());

            rideEntity.setMinuteOfDay(Utils.getMinuteOfDay(rideEntity.getTimeStamp()));
            rideEntity.setWeekday(Utils.getWeekday(rideEntity.getTimeStamp()));


            LegEntity legEntity = new LegEntity();

            ArrayList<Point> coordinates = new ArrayList<>();
            mapMatchedRideBeans.forEach(ride -> {
                Point point = new Point(Double.parseDouble(ride.getLon()), Double.parseDouble(ride.getLat()));
                coordinates.add(point);
            });

            legEntity.setGeometry(new GeoJsonLineString(coordinates));
            ArrayList array = new ArrayList<String>();
            array.add("asd");
            array.add("fads");
            legEntity.setProperties(new LegPropertyEntity(array));

            legRepository.save(legEntity);


            rideRepository.save(rideEntity);

        } catch (Exception e) {
            LOG.error(String.valueOf(e));
        }
        LOG.info("Ride parser complete {} ", this.fileName);


    }

    @NotNull
    private RideEntity getRideEntity(String[] arrOfStr, List<RideCSV> rideBeans) {
        RideEntity rideEntity = new RideEntity();
        rideEntity.setId(this.fileName);
        rideEntity.setFileId(this.fileName);
        rideEntity.setAppVersion(arrOfStr[0]);
        rideEntity.setFileVersion(Integer.parseInt(arrOfStr[1]));

        ArrayList<Position> coordinates = new ArrayList<>();

        rideBeans.forEach(ride -> {
            List<Double> places = Arrays.asList(Double.parseDouble(ride.getLon()), Double.parseDouble(ride.getLat()));
            Position pos = new Position(places);
            coordinates.add(pos);
        });
        LineString coordinatesMulti = new LineString(coordinates);
        rideEntity.setLocation(coordinatesMulti);

        ArrayList<Long> ts = new ArrayList<>();
        rideBeans.forEach(ride -> ts.add((ride).getTimeStamp()));
        rideEntity.setTs(ts);

        rideEntity.setAddedAt(new Date());
        return rideEntity;
    }

    /*
     * heuristic approach
     *
     * ride will be classified as 'forgot to stop' when User does not
     * exceed ${min_distance_to_cover_by_user_in_5_min} in 5min (300sec) (300*6000millis)
     *
     * 5min in 3sec steps = 100steps
     */
    private boolean isUserForgotToStopRecording(List<RideCSV> rideCSVList) {

        for (int i = 0; i < rideCSVList.size(); i++) {
            double cumulatedDistance = 0d;
            for (int j = 0; j < 100; j++) {
                try {
                    cumulatedDistance += Utils.calcDistance(
                            Double.parseDouble(rideCSVList.get(i + j).getLat()),
                            Double.parseDouble(rideCSVList.get(i + j).getLon()),
                            Double.parseDouble(rideCSVList.get(i + j + 1).getLat()),
                            Double.parseDouble(rideCSVList.get(i + j + 1).getLon()));
                } catch (IndexOutOfBoundsException e) {
                    break;
                }
            }
            if (cumulatedDistance < minDistanceToCoverByUserIn5Min) {
                return true;
            }
        }
        return false;
    }
}
