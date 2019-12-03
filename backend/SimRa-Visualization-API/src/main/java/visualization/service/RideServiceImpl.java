package visualization.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.geo.Point;
import org.springframework.data.mongodb.core.geo.GeoJsonGeometryCollection;
import org.springframework.data.mongodb.core.geo.GeoJsonLineString;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.core.geo.GeoJsonPolygon;
import org.springframework.stereotype.Service;
import visualization.data.mongodb.RideRepository;
import visualization.data.mongodb.entities.RideEntity;
import visualization.web.resources.RideResource;
import visualization.web.resources.serializers.RideResourceMapper;

import java.util.ArrayList;
import java.util.Arrays;
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
    public List<RideResource> getRidesAtTime(Long fromTs, Long untilTs) {
        return null;
    }

    @Override
    public List<RideResource> getRidesInRange(double latitude, double longitude, int maxDistance) {
        GeoJsonPoint point = new GeoJsonPoint(longitude, latitude);
        List<RideEntity> rideEntities = rideRepository.findByLocationNear(point, maxDistance);
        List<RideResource> rideResources = rideEntities.stream().map(rideEntity -> rideResourceMapper.mapRideEntityToResource(rideEntity)).collect(Collectors.toList());

        return rideResources;
    }

    @Override
    public GeoJsonGeometryCollection getRidesMapMatchedInRange(double longitude, double latitude, int maxDistance) {
        GeoJsonPoint point = new GeoJsonPoint(longitude, latitude);
        List<RideResource> rideResources = new ArrayList<>();
        //List<RideEntity> rideEntities = rideRepository.findByLocationNear(point, maxDistance);
        //rideResources = mapRideEntityToResource(rideEntities, rideResources, true);

        RideResource ride1 = new RideResource();
        RideResource ride2 = new RideResource();
        RideResource ride3 = new RideResource();

        Point[] array1 = {
                new Point(0d, 0d),
                new Point(1d, 1d),
                new Point(2d, 1d),
                new Point(3d, 2d),
                new Point(4d, 1d),
                new Point(5d, 1d),
                new Point(6d, 1d)
        };
        GeoJsonLineString geoJson1 = new GeoJsonLineString(Arrays.asList(array1));


        Point[] array2 = {
                new Point(0d, 2d),
                new Point(1d, 1d),
                new Point(2d, 1d),
                new Point(3d, 0d),
                new Point(4d, 1d),
                new Point(5d, 1d),
                new Point(6d, 1d)
        };
        GeoJsonLineString geoJson2 = new GeoJsonLineString(Arrays.asList(array2));


        Point[] array3 = {
                new Point(4d, 2d),
                new Point(4d, 1d),
                new Point(5d, 1d),
                new Point(6d, 2d)
        };
        GeoJsonLineString geoJson3 = new GeoJsonLineString(Arrays.asList(array3));
        ride1.setGeometry(geoJson1);
        ride2.setGeometry(geoJson2);
        ride3.setGeometry(geoJson3);

        rideResources.add(ride1);
        rideResources.add(ride2);
        rideResources.add(ride3);

        GeoJsonGeometryCollection result = new RoutePartitioner().partitionRoutes(rideResources);

        return null;
    }

}
