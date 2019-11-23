package visualization.service;

import visualization.web.resources.RideResource;

import java.util.List;

public interface RideService {

    List<RideResource> getRidesInRange(double latitude, double longitude, int maxDistance);

    RideResource getRideById(String rideId);

    List<RideResource> getRidesWithin(double[] bottomLeft, double[] upperRight);
}
