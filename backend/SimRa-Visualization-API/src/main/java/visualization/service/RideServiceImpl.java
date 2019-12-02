package visualization.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.core.geo.GeoJsonPolygon;
import org.springframework.stereotype.Service;
import visualization.data.mongodb.RideRepository;
import visualization.data.mongodb.entities.RideEntity;
import visualization.web.resources.RideResource;
import visualization.web.resources.serializers.RideResourceMapper;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


/*

This is the place where we can do some number crunching and other postprocessing for Rides

 */
@Service
public class RideServiceImpl implements RideService {

    @Autowired
    private RideRepository rideRepository;

    @Autowired
    private RideResourceMapper rideResourceMapper;

    @Override
    public RideResource getRideById(String rideId) {

        Optional<RideEntity> optionalRideEntity = rideRepository.findById(rideId);
        RideEntity rideEntity = optionalRideEntity.get();
        RideResource rideResource = rideResourceMapper.mapRideEntityToResource(rideEntity);

        return rideResource;
    }

    @Override
    public List<RideResource> getRidesWithin(GeoJsonPoint first, GeoJsonPoint second, GeoJsonPoint third, GeoJsonPoint fourth) {

        GeoJsonPolygon polygon = new GeoJsonPolygon(first, second, third, fourth, first);
        List<RideEntity> rideEntities = rideRepository.findByLocationWithin(polygon);
        List<RideResource> rideResources = rideEntities.stream().map(rideEntity -> rideResourceMapper.mapRideEntityToResource(rideEntity)).collect(Collectors.toList());

        return rideResources;
    }

    @Override
    public List<RideResource> getRidesInRange(double latitude, double longitude, int maxDistance) {
        GeoJsonPoint point = new GeoJsonPoint(longitude, latitude);
        List<RideEntity> rideEntities = rideRepository.findByLocationNear(point, maxDistance);
        List<RideResource> rideResources = rideEntities.stream().map(rideEntity -> rideResourceMapper.mapRideEntityToResource(rideEntity)).collect(Collectors.toList());

        return rideResources;
    }

}
