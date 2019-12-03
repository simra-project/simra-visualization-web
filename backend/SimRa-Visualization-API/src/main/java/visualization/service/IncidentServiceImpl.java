package visualization.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.core.geo.GeoJsonPolygon;
import org.springframework.stereotype.Service;
import visualization.data.mongodb.IncidentRepository;
import visualization.data.mongodb.entities.IncidentEntity;
import visualization.web.resources.IncidentResource;
import visualization.web.resources.serializers.IncidentResourceMapper;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


/*

This is the place where we can do some number crunching and other postprocessing for Incidents

 */
@Service
public class IncidentServiceImpl implements IncidentService {

    @Autowired
    private IncidentRepository incidentRepository;

    @Autowired
    private IncidentResourceMapper incidentResourceMapper;


    @Override
    public IncidentResource getIncident(String rideId, String key) {

        IncidentResource incidentResource;
        IncidentEntity.CompositeKey compositeKey = new IncidentEntity.CompositeKey(rideId, key);

        Optional<IncidentEntity> optionalIncidentEntity = incidentRepository.findById(compositeKey);
        IncidentEntity incidentEntity = optionalIncidentEntity.get();
        incidentResource = incidentResourceMapper.mapEntityToResource(incidentEntity);

        return incidentResource;
    }

    @Override
    public List<IncidentResource> getIncidentsByRideId(String rideId) {

        List<IncidentResource> incidents;
        List<IncidentEntity> incidentEntities = incidentRepository.findByRideId(rideId);
        incidents = incidentEntities.stream().map(incidentEntity -> incidentResourceMapper.mapEntityToResource(incidentEntity)).collect(Collectors.toList());
        return incidents;
    }

    @Override
    public List<IncidentResource> getIncidentsInRange(double longitude, double latitude, int maxDistance) {

        GeoJsonPoint point = new GeoJsonPoint(longitude, latitude);

        List<IncidentResource> incidents;
        List<IncidentEntity> incidentEntities = incidentRepository.findByLocationNear(point, maxDistance);
        incidents = incidentEntities.stream().map(incidentEntity -> incidentResourceMapper.mapEntityToResource(incidentEntity)).collect(Collectors.toList());
        return incidents;
    }

    @Override
    public List<IncidentResource> getIncidentsInWithin(GeoJsonPoint first, GeoJsonPoint second, GeoJsonPoint third, GeoJsonPoint fourth) {

        GeoJsonPolygon polygon = new GeoJsonPolygon(first, second, third, fourth, first);
        List<IncidentResource> incidents;
        List<IncidentEntity> incidentEntities = incidentRepository.findByLocationWithin(polygon);
        incidents = incidentEntities.stream().map(incidentEntity -> incidentResourceMapper.mapEntityToResource(incidentEntity)).collect(Collectors.toList());
        return incidents;
    }

}
