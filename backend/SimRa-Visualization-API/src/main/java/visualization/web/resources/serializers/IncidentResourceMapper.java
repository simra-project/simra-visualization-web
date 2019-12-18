package visualization.web.resources.serializers;

import org.springframework.stereotype.Component;
import visualization.data.mongodb.entities.IncidentEntity;
import visualization.web.resources.IncidentResource;
import visualization.web.resources.IncidentResourceProperty;

import java.util.Optional;

@Component
public class IncidentResourceMapper {

    public IncidentResource mapEntityToResource(IncidentEntity incidentEntity) {

        IncidentResource incidentResource = new IncidentResource();
        IncidentResourceProperty incidentResourceProperty = new IncidentResourceProperty();

        incidentResourceProperty.setRideId(incidentEntity.getFileId());
        incidentResourceProperty.setKey(incidentEntity.getId().getKey());
        incidentResourceProperty.setTs(incidentEntity.getTs());
        incidentResourceProperty.setChild(incidentEntity.getChildCheckBox());
        incidentResourceProperty.setTrailer(incidentEntity.getTrailerCheckBox());
        incidentResourceProperty.setScary(incidentEntity.getScary());
        incidentResourceProperty.setPhoneLocation(incidentEntity.getPLoc());
        incidentResourceProperty.setDescription(incidentEntity.getDescription());
        incidentResourceProperty.setI1Bus(incidentEntity.getI1());
        incidentResourceProperty.setI2Cyclist(incidentEntity.getI2());
        incidentResourceProperty.setI3Pedestrian(incidentEntity.getI3());
        incidentResourceProperty.setI4DeliveryVan(incidentEntity.getI4());
        incidentResourceProperty.setI5Truck(incidentEntity.getI5());
        incidentResourceProperty.setI6Motorcycle(incidentEntity.getI6());
        incidentResourceProperty.setI7Car(incidentEntity.getI7());
        incidentResourceProperty.setI8Taxi(incidentEntity.getI8());
        incidentResourceProperty.setI9Other(incidentEntity.getI9());
        incidentResourceProperty.setI10EScooter(incidentEntity.getI10());
        incidentResourceProperty.setIncidentType(incidentEntity.getIncident());

        incidentResource.setGeometry(incidentEntity.getLocation());
        incidentResource.setProperties(incidentResourceProperty);

        return incidentResource;
    }
}
