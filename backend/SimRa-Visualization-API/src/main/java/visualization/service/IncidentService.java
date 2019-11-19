package visualization.service;

import visualization.web.resources.IncidentResource;

import java.util.List;

public interface IncidentService {

    IncidentResource getIncident(String rideId, String key);

    List<IncidentResource> getIncidentsByRideId(String rideId);

    List<IncidentResource> getIncidentsInRange(double latitude, double longitude, int maxDistance);

}
