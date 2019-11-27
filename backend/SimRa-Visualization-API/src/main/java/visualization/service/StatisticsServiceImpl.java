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
import java.util.concurrent.atomic.DoubleAccumulator;
import java.util.concurrent.atomic.LongAccumulator;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
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

        statisticsResource.setRidesStatistics(getRidesStatistics(region));
        statisticsResource.setIncidentsStatistics(getIncidentStatistics(region));

        return statisticsResource;
    }

    private RideStatisticsResource getRidesStatistics(String region) {
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

    private IncidentStatisticsResource getIncidentStatistics(String region) {
        List<IncidentEntity> incidents = incidentRepository.findAll();
        List<IncidentEntity> actualIncidents = incidents.stream().filter(x -> x.getIncident() != 0).collect(Collectors.toList());

        IncidentStatisticsResource incidentStatisticsResource = new IncidentStatisticsResource();
        incidentStatisticsResource.setIncidentCount(actualIncidents.size());

        Supplier<Stream<IncidentEntity>> incidentStream = actualIncidents::stream;
        incidentStatisticsResource.setCountOfScary((int) incidentStream.get().filter(IncidentEntity::getScary).count());
        incidentStatisticsResource.setCountChildrenInvolved((int) incidentStream.get().filter(IncidentEntity::getChildCheckBox).count());
        incidentStatisticsResource.setCountTrailersInvolved((int) incidentStream.get().filter(IncidentEntity::getTrailerCheckBox).count());

        incidentStatisticsResource.setBikeTypeLabels(new ArrayList<>(BIKE_TYPES));
        List<Integer> countEachBikeType = IntStream.range(0, INCIDENT_TYPES.size())
                .mapToObj(i -> (int) incidents.stream().filter(x -> x.getBike() == i).count())
                .collect(Collectors.toList());
        incidentStatisticsResource.setBikeTypeData(new ArrayList<>(countEachBikeType));

        incidentStatisticsResource.setIncidentTypeLabels(new ArrayList<>(INCIDENT_TYPES));
        List<Integer> countEachIncidentType = IntStream.range(0, INCIDENT_TYPES.size())
                .mapToObj(i -> (int) actualIncidents.stream().filter(x -> x.getIncident() == i).count())
                .collect(Collectors.toList());
        incidentStatisticsResource.setIncidentTypeData(new ArrayList<>(countEachIncidentType));

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
