package main.java.com.simra.app.csvimporter.services;

import com.mongodb.Block;
import com.mongodb.client.MongoCollection;
import main.java.com.simra.app.csvimporter.model.Statistic;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.DoubleAccumulator;
import java.util.concurrent.atomic.LongAccumulator;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.mongodb.client.model.Filters.*;

public class StatisticsService {
    private static final List<String> BIKE_TYPES = Arrays.asList("Not Chosen", "City-/Trekking Bike", "Road Racing Bike", "E-Bike", "Recumbent Bicycle", "Freight Bicycle", "Tandem Bicycle", "Mountainbike", "Other");
    private static final List<String> INCIDENT_TYPES = Arrays.asList("Nothing", "Close Pass", "Someone pulling in or out", "Near left or right hook", "Someone approaching head on", "Tailgating", "Near-Dooring", "Dodging an Obstacle", "Other");
    private static final List<String> PARTICIPANT_TYPES = Arrays.asList("Bus", "Cyclist", "Pedestrian", "Delivery Van", "Truck", "Motorcycle", "Car", "Taxi", "Other", "E-Scooter");

    public void calculateStatistics() {
        System.out.println("Calculating statistics ...");
        DBService dbService = new DBService();

        MongoCollection<Document> ridesCollection = dbService.getRidesCollection();
        MongoCollection<Document> incidentsCollection = dbService.getIncidentsCollection();
        MongoCollection<Document> statisticsCollection = dbService.getStatisticsCollection();

        ridesCollection.distinct("region", String.class).forEach((Block<? super String>) region -> {
            System.out.print(" > " + region + " ...");

            Statistic statistic = new Statistic();
            statistic.region = region;

            calculateRideStatistics(statistic, ridesCollection);
            calculateIncidentStatistics(statistic, incidentsCollection);

            statisticsCollection.insertOne(statistic.toDocumentObject());
            System.out.println(" DONE!");
        });

        System.out.println("Finished statistics and saved it to database.");
    }

    private void calculateRideStatistics(Statistic statistic, MongoCollection<Document> rides) {
        DoubleAccumulator accumulatedDistance = new DoubleAccumulator(Double::sum, 0.d);
        LongAccumulator accumulatedDuration = new LongAccumulator(Long::sum, 0L);

        rides.find().forEach((Block<? super Document>) ride -> {
            Double distance = ride.getDouble("distance");
            Long duration = ride.getLong("duration");

            if (distance != null && duration != null) {
                accumulatedDistance.accumulate(distance);
                accumulatedDuration.accumulate(duration);
            }
        });

        statistic.rideCount = (int) rides.count();
        statistic.accumulatedDistance = accumulatedDistance.floatValue();
        statistic.accumulatedDuration = accumulatedDuration.intValue();
        statistic.accumulatedSavedCO2 = (float) (statistic.accumulatedDistance * 0.183);

        // TODO: averages hier anstatt im frontend?
    }

    private void calculateIncidentStatistics(Statistic statistic, MongoCollection<Document> incidents) {
        Bson filter = ne("incident", 0);

        statistic.incidentCount = (int) incidents.count(filter);
        statistic.incidentCountScary = (int) incidents.count(and(filter, eq("scary", true)));
        statistic.incidentCountWithChildrenInvolved = (int) incidents.count(and(filter, eq("childCheckBox", true)));
        statistic.incidentCountWithTrailersInvolved = (int) incidents.count(and(filter, eq("trailerCheckBox", true)));

        statistic.incidentBikeTypeLabels = BIKE_TYPES;
        statistic.incidentBikeTypeData = IntStream.range(0, BIKE_TYPES.size())
                .mapToObj(i1 -> (int) incidents.count(eq("bike", i1)))
                .collect(Collectors.toList());

        statistic.incidentTypeLabels = INCIDENT_TYPES;
        statistic.incidentTypeData = IntStream.range(0, INCIDENT_TYPES.size())
                .mapToObj(i -> (int) incidents.count(and(filter, eq("incident", i))))
                .collect(Collectors.toList());

        statistic.incidentParticipantTypeLabels = PARTICIPANT_TYPES;
        statistic.incidentParticipantTypeData = Arrays.asList(
                (int) incidents.count(and(filter, eq("i1", true))),
                (int) incidents.count(and(filter, eq("i2", true))),
                (int) incidents.count(and(filter, eq("i3", true))),
                (int) incidents.count(and(filter, eq("i4", true))),
                (int) incidents.count(and(filter, eq("i5", true))),
                (int) incidents.count(and(filter, eq("i6", true))),
                (int) incidents.count(and(filter, eq("i7", true))),
                (int) incidents.count(and(filter, eq("i8", true))),
                (int) incidents.count(and(filter, eq("i9", true))),
                (int) incidents.count(and(filter, eq("i10", true)))
        );
    }
}
