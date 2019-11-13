package visualization.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import visualization.data.mongodb.IncidentRepository;
import visualization.data.mongodb.entities.IncidentEntity;
import visualization.web.resources.IncidentResource;
import visualization.web.resources.geoJSON.Point;

import java.util.ArrayList;
import java.util.List;


/*

This is the place where we can do some number crunching and other postprocessing for Incidents

 */

@Service
public class IncidentServiceImpl implements IncidentService {

    @Autowired
    private IncidentRepository incidentRepository;


    @Override
    public IncidentResource getIncident(String rideId, int key) {
        // TODO: create rideIdKey from rideId and key

        //IncidentEntity incidentEntity = incidentRepository.findById(rideIdKey);
        // TODO: transform Incident Entity to incidentResource
        return null;
    }

    @Override
    public List<IncidentResource> getIncidentsByRideId(String rideId) {

        List<IncidentResource> incidents = new ArrayList();
        List<IncidentEntity> incidentEntities = incidentRepository.findByRideId(rideId);
        System.out.println(incidentEntities.toString());
        // TODO: transform Incident Entities to Incident Resources
        return incidents;
    }

    @Override
    public List<IncidentResource> getIncidentsInRange(double latitude, double longitude, int minDistance, int maxDistance) {
        Point location = new Point(latitude, longitude);
        //incidentRepository.findByLocationNear(location, minDistance, maxDistance);
        return null;
    }

}
