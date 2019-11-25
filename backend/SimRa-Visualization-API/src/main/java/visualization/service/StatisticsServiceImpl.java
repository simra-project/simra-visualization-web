package visualization.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import visualization.data.mongodb.IncidentRepository;
import visualization.data.mongodb.RideRepository;
import visualization.data.mongodb.entities.IncidentEntity;
import visualization.data.mongodb.entities.RideEntity;
import visualization.web.resources.IncidentStatisticsResource;
import visualization.web.resources.RideStatisticsResource;
import visualization.web.resources.StatisticsResource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.DoubleAccumulator;
import java.util.concurrent.atomic.LongAccumulator;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;


/*

This is the place where we can do some number crunching and other postprocessing for Statistics

 */
@Service
public class StatisticsServiceImpl implements StatisticsService {

    @Autowired
    private RideRepository rideRepository;

    @Autowired
    private IncidentRepository incidentRepository;

    @Override
//    public StatisticsResource getFilteredStatistics(Long fromTs, Long untilTs, Integer fromMinutesOfDay, Integer untilMinutesOfDay, List<String> weekdays, List<Integer> bikeTypes, List<Integer> incidentTypes, Boolean childInvolved, Boolean trailerInvolved, Boolean scary, List<Boolean> participants) {
    public StatisticsResource getStatistics(String region) {
        // TODO: use region argument once database has a region column/field

        StatisticsResource statisticsResource = new StatisticsResource();

        statisticsResource.setRidesStatistics(getFilteredRidesStatistics(/*fromTs, untilTs*/));
        statisticsResource.setIncidentsStatistics(getFilteredIncidentStatistics(/*fromTs, untilTs, fromMinutesOfDay, untilMinutesOfDay, weekdays, bikeTypes, incidentTypes, childInvolved, trailerInvolved, scary, participants*/));

        return statisticsResource;
    }

    @Override
    public StatisticsResource getStatisticsDebug(String region) {
        StatisticsResource statisticsResource = new StatisticsResource();

        int rides = 20394;
        float distance = rides * 3200.0f;
        RideStatisticsResource rideResource = new RideStatisticsResource();
        rideResource.setRideCount(rides);
        rideResource.setAccumulatedDistance(distance); // avg distance = 3,2km
        rideResource.setAccumulatedDuration(rides * 21); // avg duration = 21min
        rideResource.setAccumulatedSavedCO2(distance * 0.183f);
        statisticsResource.setRidesStatistics(rideResource);

        int incidents = 5364;
        IncidentStatisticsResource incidentResource = new IncidentStatisticsResource();
        incidentResource.setIncidentCount(incidents);
        incidentResource.setCountOfScary(ThreadLocalRandom.current().nextInt(100, incidents - 100));

        incidentResource.setBikeTypeLabels(new ArrayList<>(Arrays.asList("Type 1", "Type 2", "Type 3", "Type 4", "Type 5", "Type 6", "Type 7")));
        incidentResource.setBikeTypeData(divider(incidents, 7));

        incidentResource.setIncidentTypeLabels(new ArrayList<>(Arrays.asList("Type 1", "Type 2", "Type 3", "Type 4", "Type 5", "Type 6", "Type 7", "Type 8", "Type 9")));
        incidentResource.setIncidentTypeData(divider(incidents, 9));

        incidentResource.setParticipantTypeLabels(new ArrayList<>(Arrays.asList("Bus", "Cyclist", "Pedestrian", "Delivery Van", "Truck", "Motorcycle", "Car", "Taxi", "Other", "E-Scooter")));
        incidentResource.setParticipantTypeData(divider(incidents, 10));

        statisticsResource.setIncidentsStatistics(incidentResource);

        return statisticsResource;
    }

    // copied form Stackoverflow, seems to work for debugging
    private ArrayList<Integer> divider(int number, int divisions) {
        Random rand = new Random();
        int[] container = new int[divisions];

        while (number > 0) {
            container[rand.nextInt(divisions)]++;
            number--;
        }

        return Arrays.stream(container).boxed().collect(Collectors.toCollection(ArrayList::new));
    }



    // TODO: use filters for map filters
    private RideStatisticsResource getFilteredRidesStatistics(/*Long fromTs, Long untilTs*/) {
//        Optional<List<RideEntity>> optionalRides = rideRepository.findAllByTsBetween(fromTs, untilTs);
        List<RideEntity> rideList = rideRepository.findAll();

        RideStatisticsResource rideStatisticsResource = new RideStatisticsResource();

        AtomicInteger rideCount = new AtomicInteger();
        DoubleAccumulator accumulatedDistance = new DoubleAccumulator(Double::sum, 0.d);
        LongAccumulator accumulatedDuration = new LongAccumulator(Long::sum, 0L);

//        optionalRides.ifPresent(rideList -> {
                    rideCount.set(rideList.size());
                    rideList.forEach(ride -> {
                        // Even if only one of them is null, it shouldn't be counted as it will distort the statistics
                        if (ride.getDistance() != null && ride.getDuration() != null) {
                            accumulatedDistance.accumulate(ride.getDistance());
                            accumulatedDuration.accumulate(ride.getDuration());
                        }
                    });
//                }
//        );

        rideStatisticsResource.setRideCount(rideCount.get());
        rideStatisticsResource.setAccumulatedDistance(accumulatedDistance.floatValue());
        rideStatisticsResource.setAccumulatedDuration(accumulatedDuration.intValue());
        rideStatisticsResource.setAccumulatedSavedCO2((float) (rideStatisticsResource.getAccumulatedDistance() * 0.183));

        return rideStatisticsResource;
    }

    // TODO: use filters for map filters
    private IncidentStatisticsResource getFilteredIncidentStatistics(/*Long fromTs, Long untilTs, Integer fromMinutesOfDay, Integer untilMinutesOfDay, List<String> weekdays, List<Integer> bikeTypes, List<Integer> incidentTypes, Boolean childInvolved, Boolean trailerInvolved, Boolean scary, List<Boolean> participants*/) {
//        List<IncidentEntity> optionalIncidents = incidentRepository.findFilteredIncidents(fromTs, untilTs, fromMinutesOfDay, untilMinutesOfDay, weekdays, bikeTypes, incidentTypes, childInvolved, trailerInvolved, scary, participants);
        List<IncidentEntity> optionalIncidents = incidentRepository.findAll();

        IncidentStatisticsResource incidentStatisticsResource = new IncidentStatisticsResource();

        Integer[] countEachBikeType = {0, 0, 0, 0, 0, 0, 0, 0, 0}; //todo was wenn neuer Bike type dazu kommt?
        Integer[] countEachIncidentType = {0, 0, 0, 0, 0, 0, 0, 0, 0}; //todo was wenn incident Type type dazu kommt?


        if (!optionalIncidents.isEmpty()) {
            optionalIncidents.forEach(incident -> {
                countEachBikeType[incident.getBike()] += 1;
                countEachIncidentType[incident.getIncident()] += 1;
            });

            Supplier<Stream<IncidentEntity>> incidentStream
                    = optionalIncidents::stream;
//            incidentStatisticsResource.setCountEachBikeType(new ArrayList<>(Arrays.asList(countEachBikeType)));
//            incidentStatisticsResource.setCountEachIncidentType(new ArrayList<>(Arrays.asList(countEachIncidentType)));
//            incidentStatisticsResource.setCountChildrenInvolved((int) incidentStream.get().filter(IncidentEntity::getChildCheckBox).count());
//            incidentStatisticsResource.setCountTrailersInvolved((int) incidentStream.get().filter(IncidentEntity::getTrailerCheckBox).count());
//            incidentStatisticsResource.setCountOfScary((int) incidentStream.get().filter(IncidentEntity::getScary).count());
//            incidentStatisticsResource.setCountI1Bus((int) incidentStream.get().filter(IncidentEntity::getI1).count());
//            incidentStatisticsResource.setCountI2Cyclist((int) incidentStream.get().filter(IncidentEntity::getI2).count());
//            incidentStatisticsResource.setCountI3Pedestrian((int) incidentStream.get().filter(IncidentEntity::getI3).count());
//            incidentStatisticsResource.setCountI4DeliveryVan((int) incidentStream.get().filter(IncidentEntity::getI4).count());
//            incidentStatisticsResource.setCountI5Truck((int) incidentStream.get().filter(IncidentEntity::getI5).count());
//            incidentStatisticsResource.setCountI6Motorcycle((int) incidentStream.get().filter(IncidentEntity::getI6).count());
//            incidentStatisticsResource.setCountI7Car((int) incidentStream.get().filter(IncidentEntity::getI7).count());
//            incidentStatisticsResource.setCountI8Taxi((int) incidentStream.get().filter(IncidentEntity::getI8).count());
//            incidentStatisticsResource.setCountI9Other((int) incidentStream.get().filter(IncidentEntity::getI9).count());
//            incidentStatisticsResource.setCountI10EScooter((int) incidentStream.get().filter(IncidentEntity::getI10).count());
        }
        return incidentStatisticsResource;
    }
}
