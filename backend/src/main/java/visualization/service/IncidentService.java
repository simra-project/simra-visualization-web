package visualization.service;

import visualization.web.resources.IncidentResource;
import visualization.web.resources.RideResource;

public interface IncidentService {

    RideResource getIncidents();
    RideResource getIncidentsByRideId(int rideId);

}
