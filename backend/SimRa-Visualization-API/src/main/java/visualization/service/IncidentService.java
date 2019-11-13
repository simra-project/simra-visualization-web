package visualization.service;

import visualization.web.resources.IncidentResource;

import java.util.List;

public interface IncidentService {

    IncidentResource getIncident(int rideId, int key);

    List<IncidentResource> getIncidentsByRideId(int rideId);

    List<IncidentResource> getIncidentsInRange(double latitude, double longitude, int minDistance, int maxDistance);

}
