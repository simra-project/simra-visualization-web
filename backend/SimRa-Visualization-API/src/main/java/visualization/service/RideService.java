package visualization.service;

import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import visualization.web.resources.RideResource;

import java.util.List;

public interface RideService {

    List<RideResource> getRidesInRange(double latitude, double longitude, int maxDistance);

    RideResource getRideById(String rideId);

    List<RideResource> getRidesWithin(GeoJsonPoint first, GeoJsonPoint second, GeoJsonPoint third, GeoJsonPoint fourth);

    List<RideResource> getRidesAtTime(Long fromTs, Long untilTs);

    Boolean isRideImported(String rideId);

    Long getImportedRidesCount();

    List<RideResource> getFilteredRides(GeoJsonPoint first, GeoJsonPoint second, GeoJsonPoint third, GeoJsonPoint fourth, Long fromTs, Long untilTs, Integer fromMinutesOfDay, Integer untilMinutesOfDay, String[] weekdays);

}
