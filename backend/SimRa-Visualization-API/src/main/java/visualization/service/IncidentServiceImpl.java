package visualization.service;

import com.mongodb.client.model.geojson.Point;
import com.mongodb.client.model.geojson.Position;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.stereotype.Service;
import visualization.data.mongodb.IncidentRepository;
import visualization.data.mongodb.entities.IncidentEntity;
import visualization.web.resources.IncidentResource;

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
                    incidentResource.setCoordinates(incidentEntity.getLocation());
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
    public List<IncidentResource> getIncidentsInRange(double longitude, double latitude, int maxDistance) {
        //Position coordinates = new Position(longitude, latitude);
        GeoJsonPoint point = new GeoJsonPoint(longitude, latitude);
        System.out.println("wir sind hier 1. " + longitude + " " + latitude);
        //incidentRepository.findByLocationNear(location, minDistance, maxDistance);
        List<IncidentResource> incidentResources = new ArrayList<IncidentResource>();
        List<IncidentEntity> incidentEntities = incidentRepository.findByLocationNear(point, maxDistance);
        System.out.println("wir sind hier 2. ");
        for(IncidentEntity incidentEntity:incidentEntities) {
            IncidentResource incidentResource = new IncidentResource();
            incidentResource.setRideId(incidentEntity.getRideId());
            incidentResource.setKey(incidentEntity.getKey());
            incidentResource.setCoordinates(incidentEntity.getLocation());
            incidentResource.setTs(incidentEntity.getTs());
            incidentResources.add(incidentResource);
        }
        return incidentResources;


    }
}
