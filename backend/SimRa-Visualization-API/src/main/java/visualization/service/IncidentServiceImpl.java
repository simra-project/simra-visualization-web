package visualization.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import visualization.data.mongodb.IncidentRepository;
import visualization.data.mongodb.entities.IncidentEntity;
import visualization.web.resources.IncidentResource;
import visualization.web.resources.geoJSON.Point;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


/*

This is the place where we can do some number crunching and other postprocessing for Incidents

 */

@Service
public class IncidentServiceImpl implements IncidentService {

    @Autowired
    private IncidentRepository incidentRepository;


    @Override
    public IncidentResource getIncident(int rideId, int key) {
        // TODO: create rideIdKey from rideId and key

        IncidentResource incidentResource = new IncidentResource();
        IncidentEntity.CompositeKey compositeKey = new IncidentEntity.CompositeKey(rideId, key);
        Optional<IncidentEntity> optional = incidentRepository.findById(compositeKey);
        optional.ifPresent(incidentEntity -> {
                    incidentResource.setRideId(incidentEntity.getRideId());
                    incidentResource.setKey(incidentEntity.getKey());
                    incidentResource.setCoordinates(incidentEntity.getCoordinates());
                    incidentResource.setTs(incidentEntity.getTs());
                }
        );
        return incidentResource;
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
    public List<IncidentResource> getIncidentsInRange(double latitude, double longitude, int maxDistance) {
        Point coordinates = new Point(latitude, longitude);
        //incidentRepository.findByLocationNear(location, minDistance, maxDistance);
        IncidentResource incidentResource = new IncidentResource();
        List<IncidentEntity> incidentEntities = incidentRepository.findByCoordinatesNear(coordinates, maxDistance);

        incidentResource.setRideId(incidentEntity.getRideId());
        incidentResource.setKey(incidentEntity.getKey());
        incidentResource.setCoordinates(incidentEntity.getCoordinates());
        incidentResource.setTs(incidentEntity.getTs());
        return incidentResource;


    }
}
