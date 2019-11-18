package visualization.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.stereotype.Service;
import visualization.data.mongodb.IncidentRepository;
import visualization.data.mongodb.RideRepository;
import visualization.data.mongodb.entities.IncidentEntity;
import visualization.data.mongodb.entities.RideEntity;
import visualization.web.resources.IncidentResource;
import visualization.web.resources.RideResource;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


/*

This is the place where we can do some number crunching and other postprocessing for Rides

 */

@Service
public class RideServiceImpl implements RideService {


    @Autowired
    private RideRepository rideRepository;

    @Override
    public List<RideResource> getRidesInRange(double latitude, double longitude, int maxDistance) {
        GeoJsonPoint point = new GeoJsonPoint(longitude, latitude);
        List<RideResource> rideResources = new ArrayList<RideResource>();
        List<RideEntity> rideEntities = rideRepository.findByLocationNear(point, maxDistance);
        for(RideEntity rideEntity:rideEntities) {
            RideResource rideResource = new RideResource();
            rideResource.setRideId(rideEntity.getRideId());
            rideResource.setCoordinates(rideEntity.getLocation());
            rideResource.setTs(rideEntity.getTs());
            rideResources.add(rideResource);
        }

        return rideResources;
    }
}
