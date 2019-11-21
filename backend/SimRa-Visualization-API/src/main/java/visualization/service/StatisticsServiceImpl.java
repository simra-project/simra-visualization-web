package visualization.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import visualization.data.mongodb.RideRepository;
import visualization.data.mongodb.entities.RideEntity;
import visualization.web.resources.RideStatisticsResource;
import visualization.web.resources.StatisticsResource;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


/*

This is the place where we can do some number crunching and other postprocessing for Statistics

 */
@Service
public class StatisticsServiceImpl implements StatisticsService {

    @Autowired
    private RideRepository rideRepository;

    @Override
    public StatisticsResource getStatistics() {
        StatisticsResource statsResource = new StatisticsResource();
        List rides = rideRepository.findAll();
        statsResource.getRidesStatistics().addAll((ArrayList) rides.stream().map(it -> parseRideMetaData((RideEntity) it)).collect(Collectors.toList()));

        return statsResource;
    }

    private RideStatisticsResource parseRideMetaData(RideEntity ride) {
        RideStatisticsResource rideStatisticsResource = new RideStatisticsResource();
        rideStatisticsResource.setStartTime(ride.getTs().get(0));
        rideStatisticsResource.setDuration((int) (ride.getTs().get(ride.getTs().size() - 1) - ride.getTs().get(0)));
        // parse more Stats Metadata for Rides here
        // saved Co2 ?
        // driven Kms?
        return rideStatisticsResource;
    }

}
