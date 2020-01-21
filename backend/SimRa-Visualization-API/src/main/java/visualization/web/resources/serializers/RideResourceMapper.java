package visualization.web.resources.serializers;

import org.springframework.stereotype.Component;
import visualization.data.mongodb.entities.RideEntity;
import visualization.web.resources.RideResource;
import visualization.web.resources.RideResourceProperty;

@Component
public class RideResourceMapper {

    public RideResource mapRideEntityToResource(RideEntity rideEntity, boolean mapMatched) {

        RideResource rideResource = new RideResource();
        RideResourceProperty rideResourceProperty = new RideResourceProperty();

<<<<<<< HEAD
        rideResourceProperty.setRideId(rideEntity.getId());
        rideResourceProperty.setDistance(rideEntity.getDistance());

        if (!mapMatched) {
            rideResourceProperty.setTs(rideEntity.getTs());
=======
            rideResourceProperty.setRideId(rideEntity.getId());
            rideResourceProperty.setTs(rideEntity.getTs());
            rideResourceProperty.setDistance(rideEntity.getDistance());
            rideResourceProperty.setDuration(rideEntity.getDuration());
>>>>>>> duration added
            rideResource.setGeometry(rideEntity.getLocation());
        } else {
            rideResourceProperty.setTs(rideEntity.getTsMapMatched());
            rideResource.setGeometry(rideEntity.getLocationMapMatched());
        }

        rideResource.setProperties(rideResourceProperty);

        return rideResource;
    }
}
