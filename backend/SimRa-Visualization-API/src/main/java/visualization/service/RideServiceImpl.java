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

import java.util.ArrayList;
import java.util.Arrays;
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
        List<RideResource> rideResources = new ArrayList<>();
        return mapRideEntityToResource(rideEntities, rideResources, false);
    }

    @Override
    public List<RideResource> getRidesInRange(double latitude, double longitude, int maxDistance) {
        GeoJsonPoint point = new GeoJsonPoint(longitude, latitude);
        List<RideResource> rideResources = new ArrayList<>();
        List<RideEntity> rideEntities = rideRepository.findByLocationNear(point, maxDistance);
        return mapRideEntityToResource(rideEntities, rideResources, false);
    }

    private List<RideResource> mapRideEntityToResource(List<RideEntity> rideEntities, List<RideResource> rideResources, boolean mapMatched) {
        for (RideEntity rideEntity : rideEntities) {
            RideResource rideResource = new RideResource();
            rideResource.setRideId(rideEntity.getId());
            if (!mapMatched) {
                rideResource.setCoordinates(rideEntity.getLocation());
                rideResource.setTs(rideEntity.getTs());
            } else {
                rideResource.setCoordinates(rideEntity.getLocationMapMatched());
                rideResource.setTs(rideEntity.getTsMapMatched());
            }
            rideResources.add(rideResource);
        }

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

        ride1.setRideId("1");
        ride2.setRideId("2");

        Point[] array1 = {
                new Point(0d, 0d),
                new Point(1d, 1d),
                new Point(2d, 1d),
                new Point(3d, 2d),
                new Point(4d, 1d),
                new Point(5d, 1d)
        };

        Point[] array2 = {
                new Point(0d, 2d),
                new Point(1d, 1d),
                new Point(2d, 1d),
                new Point(3d, 0d),
                new Point(4d, 1d),
                new Point(5d, 1d)
        };
        ride1.setCoordinates(new GeoJsonLineString(Arrays.asList(array1)));

        ride2.setCoordinates(new GeoJsonLineString(Arrays.asList(array2)));

        rideResources.add(ride1);
        rideResources.add(ride2);

        GeoJsonGeometryCollection result = new RoutePartitioner().partitionRoutes(rideResources);

        return null;
    }
}
