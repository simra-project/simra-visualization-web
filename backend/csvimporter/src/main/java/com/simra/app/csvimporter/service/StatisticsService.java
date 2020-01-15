package com.simra.app.csvimporter.service;

import com.mongodb.Block;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.simra.app.csvimporter.model.Statistic;
import org.bson.Document;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.bson.conversions.Bson;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.DoubleAccumulator;
import java.util.concurrent.atomic.LongAccumulator;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.mongodb.client.model.Filters.*;
import static com.simra.app.csvimporter.model.Statistic.ProfileAgeGroupEntry;

public class StatisticsService {
    private static final List<String> BIKE_TYPES = Arrays.asList("Not Chosen", "City-/Trekking Bike", "Road Racing Bike", "E-Bike", "Recumbent Bicycle", "Freight Bicycle", "Tandem Bicycle", "Mountainbike", "Other");
    private static final List<String> INCIDENT_TYPES = Arrays.asList("Nothing", "Close Pass", "Someone pulling in or out", "Near left or right hook", "Someone approaching head on", "Tailgating", "Near-Dooring", "Dodging an Obstacle", "Other");
    private static final List<String> PARTICIPANT_TYPES = Arrays.asList("Bus", "Cyclist", "Pedestrian", "Delivery Van", "Truck", "Motorcycle", "Car", "Taxi", "Other", "E-Scooter");
    private static final List<String> AGE_GROUPX = Arrays.asList("After 2004", "2000 to 2004", "1995 to 1999", "1990 to 1994", "1985 to 1989", "1980 to 1984", "1975 to 1979", "1970 to 1974", "1965 to 1969", "1960 to 1964", "1955 to 1959", "1950 to 1954", "Before 1950");
    private static final List<String> AGE_GROUPS = Arrays.asList("< 15",       "15 to 19",     "20 to 24",     "25 to 29",     "30 to 34",     "35 to 39",     "40 to 44",     "45 to 49",     "50 to 54",     "55 to 59",     "60 to 64",     "65 to 69",     "> 69");

    public void calculateStatistics() {
        System.out.println("Calculating statistics ...");

        CodecRegistry pojoCodecRegistry = CodecRegistries.fromRegistries(MongoClientSettings.getDefaultCodecRegistry(), CodecRegistries.fromProviders(PojoCodecProvider.builder().automatic(true).build()));
        MongoDatabase db = (new MongoClient("localhost", 27017)).getDatabase("simra").withCodecRegistry(pojoCodecRegistry);
        MongoCollection<Document> ridesCollection = db.getCollection("rides");
        MongoCollection<Document> incidentsCollection = db.getCollection("incidents");
        MongoCollection<Document> profileCollection = db.getCollection("profile");
        MongoCollection<Document> statisticsCollection = db.getCollection("statistics");

        ridesCollection.distinct("region", String.class).forEach((Block<? super String>) region -> {
            System.out.print(" > " + region + " ...");

            Statistic statistic = new Statistic();
            statistic.region = region;

            calculateRideStatistics(statistic, ridesCollection, region);
            calculateIncidentStatistics(statistic, incidentsCollection, region);
            calculateProfileStatistics(statistic, profileCollection, incidentsCollection, region);

            statisticsCollection.insertOne(statistic.toDocumentObject());
            System.out.println(" DONE!");
        });

        System.out.println("Finished statistics and saved it to database.");
    }

    private void calculateRideStatistics(Statistic statistic, MongoCollection<Document> rides, String region) {
        Bson filter = eq("region", region);
        LongAccumulator accumulatedDistance = new LongAccumulator(Long::sum, 0L);
        LongAccumulator accumulatedDuration = new LongAccumulator(Long::sum, 0L);

        rides.find(filter).forEach((Block<? super Document>) ride -> {
            Long distance = getLongNumber(ride, "distance");
            Long duration = getLongNumber(ride, "duration");

            if (distance != null && duration != null) {
                accumulatedDistance.accumulate(distance);
                accumulatedDuration.accumulate(duration);
            }
        });

        statistic.rideCount = (int) rides.countDocuments(filter);
        statistic.accumulatedDistance = accumulatedDistance.doubleValue();
        statistic.accumulatedDuration = accumulatedDuration.longValue();
        statistic.accumulatedSavedCO2 = getSavedCO2(statistic.accumulatedDistance);

        statistic.averageDistance = statistic.accumulatedDistance / (double) statistic.rideCount;
        statistic.averageDuration = statistic.accumulatedDuration / (long) statistic.rideCount;
        statistic.averageSpeed = 3.6 * statistic.accumulatedDistance / (double) (statistic.accumulatedDuration / 1000);
    }

    private void calculateIncidentStatistics(Statistic statistic, MongoCollection<Document> incidents, String region) {
        Bson filter = and(eq("region", region), ne("incident", 0));

        statistic.incidentCount = (int) incidents.countDocuments(filter);
        statistic.incidentCountScary = (int) incidents.countDocuments(and(filter, eq("scary", true)));
        statistic.incidentCountWithChildrenInvolved = (int) incidents.countDocuments(and(filter, eq("childCheckBox", true)));
        statistic.incidentCountWithTrailersInvolved = (int) incidents.countDocuments(and(filter, eq("trailerCheckBox", true)));

        statistic.incidentTypeLabels = INCIDENT_TYPES;
        statistic.incidentTypeData = IntStream.range(0, INCIDENT_TYPES.size())
                .mapToObj(i -> (int) incidents.countDocuments(and(filter, eq("incident", i))))
                .collect(Collectors.toList());

        statistic.incidentParticipantTypeLabels = PARTICIPANT_TYPES;
        statistic.incidentParticipantTypeData = Arrays.asList(
                (int) incidents.countDocuments(and(filter, eq("i1", true))),
                (int) incidents.countDocuments(and(filter, eq("i2", true))),
                (int) incidents.countDocuments(and(filter, eq("i3", true))),
                (int) incidents.countDocuments(and(filter, eq("i4", true))),
                (int) incidents.countDocuments(and(filter, eq("i5", true))),
                (int) incidents.countDocuments(and(filter, eq("i6", true))),
                (int) incidents.countDocuments(and(filter, eq("i7", true))),
                (int) incidents.countDocuments(and(filter, eq("i8", true))),
                (int) incidents.countDocuments(and(filter, eq("i9", true))),
                (int) incidents.countDocuments(and(filter, eq("i10", true)))
        );
    }

    private void calculateProfileStatistics(Statistic statistic, MongoCollection<Document> profiles, MongoCollection<Document> incidents, String region) {
        Bson regionFilter = eq("directoryRegion", region);

        statistic.profileCount = (int) profiles.countDocuments(regionFilter);
        statistic.profileCountMale = (int) profiles.countDocuments(and(regionFilter, eq("gender", 1)));
        statistic.profileCountFemale = (int) profiles.countDocuments(and(regionFilter, eq("gender", 2)));
        statistic.profileCountOther = (int) profiles.countDocuments(and(regionFilter, eq("gender", 3)));

        // Calculating the sum of incidents (including incident type 0) for each bike type and extrapolating the data over the profile count
        statistic.profileBikeTypeLabels = BIKE_TYPES;
        List<Integer> bikeTypes = IntStream.range(0, BIKE_TYPES.size())
                .mapToObj(i1 -> (int) incidents.countDocuments(and(eq("region", region), eq("bike", i1))))
                .collect(Collectors.toList());
        Integer bikeTypesSum = bikeTypes.stream().reduce(0, Integer::sum);
        statistic.profileBikeTypeData = bikeTypes.stream().map(i -> (int) (((float) i / bikeTypesSum) * statistic.profileCount)).collect(Collectors.toList());

        // Skipping birth = 0 because its unlabelled
        statistic.profileAgeDistributionLabels = AGE_GROUPS;
        statistic.profileAgeDistributionData = IntStream.range(1, AGE_GROUPS.size() + 1)
                .mapToObj(i -> (int) profiles.countDocuments(and(regionFilter, eq("birth", i))))
                .collect(Collectors.toList());
        statistic.profileAgeDistributionDataMale = IntStream.range(1, AGE_GROUPS.size() + 1)
                .mapToObj(i -> (int) profiles.countDocuments(and(regionFilter, eq("birth", i), eq("gender", 1))))
                .collect(Collectors.toList());
        statistic.profileAgeDistributionDataFemale = IntStream.range(1, AGE_GROUPS.size() + 1)
                .mapToObj(i -> (int) profiles.countDocuments(and(regionFilter, eq("birth", i), eq("gender", 2))))
                .collect(Collectors.toList());
        statistic.profileAgeDistributionDataOther = IntStream.range(1, AGE_GROUPS.size() + 1)
                .mapToObj(i -> (int) profiles.countDocuments(and(regionFilter, eq("birth", i), eq("gender", 3))))
                .collect(Collectors.toList());

        statistic.profileAgeGroupCrossData = IntStream.range(1, AGE_GROUPS.size() + 1).mapToObj(i -> {
            ProfileAgeGroupEntry row = new ProfileAgeGroupEntry();
            row.ageGroup = AGE_GROUPS.get(i - 1);
            row.bikerCount = (int) profiles.countDocuments(and(regionFilter, eq("birth", i)));

            LongAccumulator rideCount = new LongAccumulator(Long::sum, 0L);
            DoubleAccumulator accumulatedDistance = new DoubleAccumulator(Double::sum, 0.0d);
            LongAccumulator accumulatedDuration = new LongAccumulator(Long::sum, 0L);
            LongAccumulator scaryIncidentCount = new LongAccumulator(Long::sum, 0L);

            profiles.find(and(regionFilter, eq("birth", i))).forEach((Block<? super Document>) profile -> {
                Integer numberOfRides = profile.getInteger("numberOfRides");
                Double distance = getDoubleNumber(profile, "distance");
                Long duration = getLongNumber(profile, "duration");
                Integer scaryIncidents = profile.getInteger("numberOfScary");

                if (numberOfRides != null && numberOfRides != 0 && distance != null && distance != 0 && duration != null && duration != 0) {
                    rideCount.accumulate((long) numberOfRides);
                    accumulatedDistance.accumulate(distance);
                    accumulatedDuration.accumulate(duration);
                    scaryIncidentCount.accumulate(scaryIncidents);
                }
            });

            row.rideCount = (int) rideCount.longValue();
            row.accumulatedDistance = accumulatedDistance.doubleValue();
            row.accumulatedDuration = accumulatedDuration.longValue();
            row.accumulatedSavedCO2 = getSavedCO2(row.accumulatedDistance);
            row.scaryIncidentCount = (int) scaryIncidentCount.longValue();

            if (row.rideCount > 0) {
                row.averageDistance = row.accumulatedDistance / (double) row.rideCount;
                row.averageDuration = row.accumulatedDuration / (long) row.rideCount;
                row.averageSpeed = 3.6 * row.accumulatedDistance / (double) (row.accumulatedDuration / 1000);
                row.averageSavedCO2 = row.accumulatedSavedCO2 / (double) row.rideCount;
                row.averageScaryIncidentCount = row.scaryIncidentCount / (double) row.rideCount;
            }

            return row;
        }).collect(Collectors.toList());

        ProfileAgeGroupEntry total = statistic.profileAgeGroupCrossData.stream().reduce(new ProfileAgeGroupEntry(), (ProfileAgeGroupEntry lhs, ProfileAgeGroupEntry rhs) -> {
            lhs.bikerCount += rhs.bikerCount;
            lhs.rideCount += rhs.rideCount;
            lhs.accumulatedDuration += rhs.accumulatedDuration;
            lhs.accumulatedDistance += rhs.accumulatedDistance;
            lhs.accumulatedSavedCO2 += rhs.accumulatedSavedCO2;
            lhs.scaryIncidentCount += rhs.scaryIncidentCount;
            return lhs;
        });
        total.averageDistance = total.accumulatedDistance / (double) total.rideCount;
        total.averageDuration = total.accumulatedDuration / (long) total.rideCount;
        total.averageSpeed = 3.6 * total.accumulatedDistance / (double) (total.accumulatedDuration / 1000);
        total.averageSavedCO2 = total.accumulatedSavedCO2 / (double) total.rideCount;
        total.averageScaryIncidentCount = total.scaryIncidentCount / (double) total.rideCount;
        statistic.profileAgeGroupCrossTotal = total;
    }

    private Double getSavedCO2(double distance) {
        return (distance * 0.183) / 1000.0;
    }

    private Long getLongNumber(Document document, String key) {
        Object number = document.get(key);
        if (number == null) return null;

        if (number.getClass() == Integer.class) return ((Integer) number).longValue();
        if (number.getClass() == Long.class) return (Long) number;
        if (number.getClass() == Double.class) return ((Double) number).longValue();

        return null;
    }

    private Double getDoubleNumber(Document document, String key) {
        Object number = document.get(key);
        if (number == null) return null;

        if (number.getClass() == Integer.class) return ((Integer) number).doubleValue();
        if (number.getClass() == Long.class) return ((Long) number).doubleValue();
        if (number.getClass() == Double.class) return (Double) number;

        return null;
    }
}

