package visualization.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.core.geo.GeoJsonPolygon;
import org.springframework.stereotype.Service;
import visualization.data.mongodb.RideRepository;
import visualization.data.mongodb.entities.RideEntity;
import visualization.web.resources.LegResource;
import visualization.web.resources.RideResource;
import visualization.web.resources.serializers.RideResourceMapper;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RideServiceImpl implements RideService {

    @Autowired
    private RideRepository rideRepository;

    @Autowired
    private RideResourceMapper rideResourceMapper;

    @Autowired
    private RoutePartitioningService routePartitioningService;

    @Override
    public RideResource getRideById(String rideId) {
        RideEntity rideEntity = rideRepository.findById(rideId).get();

        return rideResourceMapper.mapRideEntityToResource(rideEntity, false);
    }

    @Override
    public List<RideResource> getRidesWithin(GeoJsonPoint first, GeoJsonPoint second, GeoJsonPoint third, GeoJsonPoint fourth) {
        GeoJsonPolygon polygon = new GeoJsonPolygon(first, second, third, fourth, first);
        List<RideEntity> rideEntities = rideRepository.findByLocationWithin(polygon);

        return rideEntities.stream().map(rideEntity -> rideResourceMapper.mapRideEntityToResource(rideEntity, false)).collect(Collectors.toList());
    }

    @Override
    public List<RideResource> getRidesInRange(double latitude, double longitude, int maxDistance) {
        GeoJsonPoint point = new GeoJsonPoint(longitude, latitude);
        List<RideEntity> rideEntities = rideRepository.findByLocationNear(point, maxDistance);

        return rideEntities.stream().map(rideEntity -> rideResourceMapper.mapRideEntityToResource(rideEntity, false)).collect(Collectors.toList());
    }

    @Override
    public List<LegResource> getRidesMapMatchedInRange(double longitude, double latitude, int maxDistance) {
        GeoJsonPoint point = new GeoJsonPoint(longitude, latitude);
        List<RideEntity> rideEntities = rideRepository.findByLocationNear(point, maxDistance);
        List<RideResource> rideResources = rideEntities.stream().map(rideEntity -> rideResourceMapper.mapRideEntityToResource(rideEntity, true)).collect(Collectors.toList());

        return routePartitioningService.partitionRoutes(rideResources);
    }

    @Override
    public List<RideResource> getRidesAtTime(Long fromTs, Long untilTs) {
        List<RideEntity> rideEntities = rideRepository.findAllByTsBetween(fromTs, untilTs);

        return rideEntities.stream().map(rideEntity -> rideResourceMapper.mapRideEntityToResource(rideEntity, false)).collect(Collectors.toList());
    }
}
