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
import java.util.stream.Collectors;

@Service
public class IncidentServiceImpl implements IncidentService {

    @Autowired
    private IncidentRepository incidentRepository;

    @Autowired
    private IncidentResourceMapper incidentResourceMapper;

    @Override
    public IncidentResource getIncident(String rideId, String key) {
        IncidentEntity.CompositeKey compositeKey = new IncidentEntity.CompositeKey(rideId, key);

        IncidentEntity incidentEntity = incidentRepository.findById(compositeKey).get();

        return incidentResourceMapper.mapEntityToResource(incidentEntity);
    }

    @Override
    public List<IncidentResource> getIncidentsByFileId(String rideId) {
        List<IncidentEntity> incidentEntities = incidentRepository.findByFileId(rideId);

        return incidentEntities.stream()
                .filter(incident -> incident.getIncident() != 0)
                .map(incident -> incidentResourceMapper.mapEntityToResource(incident))
                .collect(Collectors.toList());
    }

    @Override
    public List<IncidentResource> getIncidentsInRange(double longitude, double latitude, int maxDistance) {
        GeoJsonPoint point = new GeoJsonPoint(longitude, latitude);

        List<IncidentEntity> incidentEntities = incidentRepository.findByLocationNear(point, maxDistance);

        return incidentEntities.stream()
                .filter(incident -> incident.getIncident() != 0)
                .map(incident -> incidentResourceMapper.mapEntityToResource(incident))
                .collect(Collectors.toList());
    }

    @Override
    public List<IncidentResource> getIncidentsInWithin(GeoJsonPoint first, GeoJsonPoint second, GeoJsonPoint third, GeoJsonPoint fourth) {
        GeoJsonPolygon polygon = new GeoJsonPolygon(first, second, third, fourth, first);

        List<IncidentEntity> incidentEntities = incidentRepository.findByLocationWithin(polygon);

        return incidentEntities.stream()
                .filter(incident -> incident.getIncident() != 0)
                .map(incident -> incidentResourceMapper.mapEntityToResource(incident))
                .collect(Collectors.toList());
    }
}
