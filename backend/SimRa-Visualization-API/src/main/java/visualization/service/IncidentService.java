package visualization.service;

import visualization.data.mongodb.entities.IncidentEntity;
import visualization.web.resources.IncidentResource;
import visualization.web.resources.RideResource;

import java.util.List;

public interface IncidentService {

    //RideResource getIncidents();
    List<IncidentResource> getIncidentsByRideId(int rideId);

}
