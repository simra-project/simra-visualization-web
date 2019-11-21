package visualization.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.stereotype.Service;
import visualization.data.mongodb.IncidentRepository;
import visualization.data.mongodb.entities.IncidentEntity;
import visualization.web.resources.IncidentResource;

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


    @Override
    public IncidentResource getIncident(String rideId, String key) {

        final IncidentResource[] incidentResource = {new IncidentResource()};
        IncidentEntity.CompositeKey compositeKey = new IncidentEntity.CompositeKey(rideId, key);
        Optional<IncidentEntity> optional = incidentRepository.findById(compositeKey);
        optional.ifPresent(incidentEntity -> {
                   incidentResource[0] = mapEntityToResource(incidentEntity);

                }
        );
        return incidentResource[0];
    }

    @Override
    public List<IncidentResource> getIncidentsByRideId(String rideId) {

        List<IncidentResource> incidents;
        List<IncidentEntity> incidentEntities = incidentRepository.findByRideId(rideId);
        incidents = incidentEntities.stream().map(this::mapEntityToResource).collect(Collectors.toList());
        return incidents;
    }

    @Override
    public List<IncidentResource> getIncidentsInRange(double longitude, double latitude, int maxDistance) {

        GeoJsonPoint point = new GeoJsonPoint(longitude, latitude);

        List<IncidentResource> incidentResources;
        List<IncidentEntity> incidentEntities = incidentRepository.findByLocationNear(point, maxDistance);

        incidentResources = incidentEntities.stream().map(this::mapEntityToResource).collect(Collectors.toList());
        return incidentResources;


    }

    private IncidentResource mapEntityToResource(IncidentEntity incidentEntity) {
        IncidentResource incidentResource = new IncidentResource();
        incidentResource.setRideId(incidentEntity.getRideId());
        incidentResource.setKey(incidentEntity.getKey());
        incidentResource.setCoordinates(incidentEntity.getLocation());
        incidentResource.setTs(incidentEntity.getTs());
        incidentResource.setChild(incidentEntity.getChildCheckBox());
        incidentResource.setTrailer(incidentEntity.getTrailerCheckBox());
        incidentResource.setPhoneLocation(incidentEntity.getPLoc());
        incidentResource.setDescription(incidentEntity.getDescription());
        incidentResource.setI1Bus(incidentEntity.getI1());
        incidentResource.setI2Cyclist(incidentEntity.getI2());
        incidentResource.setI3Pedestrian(incidentEntity.getI3());
        incidentResource.setI4DeliveryVan(incidentEntity.getI4());
        incidentResource.setI5Truck(incidentEntity.getI5());
        incidentResource.setI6Motorcycle(incidentEntity.getI6());
        incidentResource.setI7Car(incidentEntity.getI7());
        incidentResource.setI8Taxi(incidentEntity.getI8());
        incidentResource.setI9Other(incidentEntity.getI9());
        incidentResource.setI10EScooter(incidentEntity.getI10());
        return incidentResource;
    }
}
