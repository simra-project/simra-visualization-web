package visualization.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.core.geo.GeoJsonPolygon;
import org.springframework.stereotype.Service;
import visualization.data.mongodb.RideRepository;
import visualization.data.mongodb.entities.RideEntity;
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
    public RideResource getRideById(String rideId) {

        RideResource rideResource = new RideResource();
        Optional<RideEntity> optional = rideRepository.findById(rideId);
        optional.ifPresent(rideEntity -> {
                    rideResource.setRideId(rideEntity.getId());
                    rideResource.setCoordinates(rideEntity.getLocation());
                    rideResource.setTs(rideEntity.getTs());
                }
        );
        return rideResource;
    }

    @Override
    public List<RideResource> getRidesWithin(GeoJsonPoint first, GeoJsonPoint second, GeoJsonPoint third, GeoJsonPoint fourth) {

        GeoJsonPolygon polygon = new GeoJsonPolygon(first, second, third, fourth, first);
        List<RideEntity> rideEntities = rideRepository.findByLocationWithin(polygon);
        System.out.println("Size of rideEntities: " + rideEntities.size());
        List<RideResource> rideResources = new ArrayList<>();
        return mapRideEntityToResource(rideEntities, rideResources);
    }

    @Override
    public List<RideResource> getRidesInRange(double latitude, double longitude, int maxDistance) {
        GeoJsonPoint point = new GeoJsonPoint(longitude, latitude);
        List<RideResource> rideResources = new ArrayList<>();
        List<RideEntity> rideEntities = rideRepository.findByLocationNear(point, maxDistance);
        return mapRideEntityToResource(rideEntities, rideResources);
    }

    private List<RideResource> mapRideEntityToResource(List<RideEntity> rideEntities, List<RideResource> rideResources) {
        for (RideEntity rideEntity : rideEntities) {
            RideResource rideResource = new RideResource();
            rideResource.setRideId(rideEntity.getId());
            rideResource.setCoordinates(rideEntity.getLocation());
            rideResource.setTs(rideEntity.getTs());
            rideResources.add(rideResource);
        }

        return rideResources;
    }
}
