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

    private static final List<String> BIKE_TYPES = Arrays.asList("Not Chosen", "City-/Trekking Bike", "Road Racing Bike", "E-Bike", "Recumbent Bicycle", "Freight Bicycle", "Tandem Bicycle", "Mountainbike", "Other");
    private static final List<String> INCIDENT_TYPES = Arrays.asList("Nothing", "Close Pass", "s.o. pulling in or out", "Near left or right hook", "s.o. approaching head on", "Tailgating", "Near-Dooring", "Dodging an Obstacle", "Other");
    private static final List<String> PARTICIPANT_TYPES = Arrays.asList("Bus", "Cyclist", "Pedestrian", "Delivery Van", "Truck", "Motorcycle", "Car", "Taxi", "Other", "E-Scooter");

    @Override
    public StatisticsResource getStatistics(String region) {
        // TODO: use region argument once database has a region column/field

        StatisticsResource statisticsResource = new StatisticsResource();

        statisticsResource.setRidesStatistics(getRidesStatistics());
        statisticsResource.setIncidentsStatistics(getIncidentStatistics());

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

    private RideStatisticsResource getRidesStatistics() {

        List<RideEntity> rideList = rideRepository.findAll();

        RideStatisticsResource rideStatisticsResource = new RideStatisticsResource();

        DoubleAccumulator accumulatedDistance = new DoubleAccumulator(Double::sum, 0.d);
        LongAccumulator accumulatedDuration = new LongAccumulator(Long::sum, 0L);

        rideList.forEach(ride -> {
            // Even if only one of them is null, it shouldn't be counted as it will distort the statistics
            if (ride.getDistance() != null && ride.getDuration() != null) {
                accumulatedDistance.accumulate(ride.getDistance());
                accumulatedDuration.accumulate(ride.getDuration());
            }
        });

        rideStatisticsResource.setRideCount(rideList.size());
        rideStatisticsResource.setAccumulatedDistance(accumulatedDistance.floatValue());
        rideStatisticsResource.setAccumulatedDuration(accumulatedDuration.intValue());
        rideStatisticsResource.setAccumulatedSavedCO2((float) (rideStatisticsResource.getAccumulatedDistance() * 0.183));

        return rideStatisticsResource;
    }

    private IncidentStatisticsResource getIncidentStatistics() {
        List<IncidentEntity> incidents = incidentRepository.findAll();

        IncidentStatisticsResource incidentStatisticsResource = new IncidentStatisticsResource();

        incidentStatisticsResource.setIncidentCount(incidents.size());


        Integer[] countEachBikeType = new Integer[BIKE_TYPES.size()];
        Arrays.fill(countEachBikeType, 0);
        Integer[] countEachIncidentType = new Integer[INCIDENT_TYPES.size()];
        Arrays.fill(countEachIncidentType, 0);

        incidents.forEach(incident -> {
            countEachBikeType[incident.getBike()] += 1;
            countEachIncidentType[incident.getIncident()] += 1;
        });


        Supplier<Stream<IncidentEntity>> incidentStream = incidents::stream;
        incidentStatisticsResource.setCountOfScary((int) incidentStream.get().filter(IncidentEntity::getScary).count());
        incidentStatisticsResource.setCountChildrenInvolved((int) incidentStream.get().filter(IncidentEntity::getChildCheckBox).count());
        incidentStatisticsResource.setCountTrailersInvolved((int) incidentStream.get().filter(IncidentEntity::getTrailerCheckBox).count());

        incidentStatisticsResource.setBikeTypeLabels(new ArrayList<>(BIKE_TYPES));
        incidentStatisticsResource.setBikeTypeData(new ArrayList<>(Arrays.asList(countEachBikeType)));

        incidentStatisticsResource.setIncidentTypeLabels(new ArrayList<>(INCIDENT_TYPES));
        incidentStatisticsResource.setIncidentTypeData(new ArrayList<>(Arrays.asList(countEachIncidentType)));

        incidentStatisticsResource.setParticipantTypeLabels(new ArrayList<>(PARTICIPANT_TYPES));

        Integer[] participantTypesCount = {
                (int) incidentStream.get().filter(IncidentEntity::getI1).count(),
                (int) incidentStream.get().filter(IncidentEntity::getI2).count(),
                (int) incidentStream.get().filter(IncidentEntity::getI3).count(),
                (int) incidentStream.get().filter(IncidentEntity::getI4).count(),
                (int) incidentStream.get().filter(IncidentEntity::getI5).count(),
                (int) incidentStream.get().filter(IncidentEntity::getI6).count(),
                (int) incidentStream.get().filter(IncidentEntity::getI7).count(),
                (int) incidentStream.get().filter(IncidentEntity::getI8).count(),
                (int) incidentStream.get().filter(IncidentEntity::getI9).count(),
                (int) incidentStream.get().filter(IncidentEntity::getI10).count()
        };
        incidentStatisticsResource.setParticipantTypeData(new ArrayList<>(Arrays.asList(participantTypesCount)));

        return incidentStatisticsResource;
    }
}
