package visualization.web.resources.serializers;

import org.springframework.stereotype.Component;
import visualization.data.mongodb.entities.RideEntity;
import visualization.web.resources.RideResource;
import visualization.web.resources.RideResourceProperty;

@Component
public class RideResourceMapper {

    public RideResource mapRideEntityToResource(RideEntity rideEntity) {

            RideResource rideResource = new RideResource();
            RideResourceProperty rideResourceProperty = new RideResourceProperty();

            rideResourceProperty.setRideId(rideEntity.getId());
            rideResourceProperty.setTs(rideEntity.getTs());
            rideResourceProperty.setDistance(rideEntity.getDistance());
            rideResourceProperty.setDuration(rideEntity.getDuration());
            rideResource.setGeometry(rideEntity.getLocation());
            rideResource.setProperties(rideResourceProperty);

        return rideResource;
    }
}
