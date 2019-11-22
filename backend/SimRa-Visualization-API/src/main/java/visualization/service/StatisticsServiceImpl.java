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
import java.util.Optional;
import java.util.concurrent.atomic.DoubleAccumulator;
import java.util.concurrent.atomic.LongAccumulator;
import java.util.function.Supplier;
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
    public StatisticsResource getFilteredStatistics(Long fromTs, Long untilTs, Integer fromMinutesOfDay, Integer untilMinutesOfDay, List<String> weekdays, List<Integer> bikeTypes, List<Integer> incidentTypes, Boolean childInvolved, Boolean trailerInvolved, Boolean scary, List<Boolean> participants) {

        StatisticsResource statisticsResource = new StatisticsResource();

        statisticsResource.setRidesStatistics(getFilteredRidesStatistics(fromTs, untilTs));
        statisticsResource.setIncidentsStatistics(getFilteredIncidentStatistics(fromTs, untilTs, fromMinutesOfDay, untilMinutesOfDay, weekdays, bikeTypes, incidentTypes, childInvolved, trailerInvolved, scary, participants));

        return statisticsResource;
    }

    private RideStatisticsResource getFilteredRidesStatistics(Long fromTs, Long untilTs) {
        Optional<List<RideEntity>> optionalRides = rideRepository.findAllByTsBetween(fromTs, untilTs);

        RideStatisticsResource rideStatisticsResource = new RideStatisticsResource();

        DoubleAccumulator accumulatedDistance = new DoubleAccumulator(Double::sum, 0.d);
        LongAccumulator accumulatedDuration = new LongAccumulator(Long::sum, 0L);

        optionalRides.ifPresent(rideList -> {
                    rideList.forEach(ride -> {
                        accumulatedDistance.accumulate(ride.getDistance());
                        accumulatedDuration.accumulate(ride.getDuration());
                    });
                }
        );

        rideStatisticsResource.setAccumulatedDistance(accumulatedDistance.floatValue());
        rideStatisticsResource.setAccumulatedDuration(accumulatedDuration.intValue());
        rideStatisticsResource.setAccumulatedSavedCO2((float) (rideStatisticsResource.getAccumulatedDistance() * 0.183));

        return rideStatisticsResource;
    }

    private IncidentStatisticsResource getFilteredIncidentStatistics(Long fromTs, Long untilTs, Integer fromMinutesOfDay, Integer untilMinutesOfDay, List<String> weekdays, List<Integer> bikeTypes, List<Integer> incidentTypes, Boolean childInvolved, Boolean trailerInvolved, Boolean scary, List<Boolean> participants) {
        List<IncidentEntity> optionalIncidents = incidentRepository.findFilteredIncidents(fromTs, untilTs, fromMinutesOfDay, untilMinutesOfDay, weekdays, bikeTypes, incidentTypes, childInvolved, trailerInvolved, scary, participants);

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
            incidentStatisticsResource.setCountEachBikeType(new ArrayList<>(Arrays.asList(countEachBikeType)));
            incidentStatisticsResource.setCountEachIncidentType(new ArrayList<>(Arrays.asList(countEachIncidentType)));
            incidentStatisticsResource.setCountChildrenInvolved((int) incidentStream.get().filter(IncidentEntity::getChildCheckBox).count());
            incidentStatisticsResource.setCountTrailersInvolved((int) incidentStream.get().filter(IncidentEntity::getTrailerCheckBox).count());
            incidentStatisticsResource.setCountOfScary((int) incidentStream.get().filter(IncidentEntity::getScary).count());
            incidentStatisticsResource.setCountI1Bus((int) incidentStream.get().filter(IncidentEntity::getI1).count());
            incidentStatisticsResource.setCountI2Cyclist((int) incidentStream.get().filter(IncidentEntity::getI2).count());
            incidentStatisticsResource.setCountI3Pedestrian((int) incidentStream.get().filter(IncidentEntity::getI3).count());
            incidentStatisticsResource.setCountI4DeliveryVan((int) incidentStream.get().filter(IncidentEntity::getI4).count());
            incidentStatisticsResource.setCountI5Truck((int) incidentStream.get().filter(IncidentEntity::getI5).count());
            incidentStatisticsResource.setCountI6Motorcycle((int) incidentStream.get().filter(IncidentEntity::getI6).count());
            incidentStatisticsResource.setCountI7Car((int) incidentStream.get().filter(IncidentEntity::getI7).count());
            incidentStatisticsResource.setCountI8Taxi((int) incidentStream.get().filter(IncidentEntity::getI8).count());
            incidentStatisticsResource.setCountI9Other((int) incidentStream.get().filter(IncidentEntity::getI9).count());
            incidentStatisticsResource.setCountI10EScooter((int) incidentStream.get().filter(IncidentEntity::getI10).count());
        }
        return incidentStatisticsResource;
    }
}
