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

        rideResourceProperty.setRideId(rideEntity.getId());
        rideResourceProperty.setDistance(rideEntity.getDistance());
        rideResourceProperty.setWeekday(rideEntity.getWeekday());

        if (!mapMatched) {
            rideResourceProperty.setTs(rideEntity.getTs());
            rideResource.setGeometry(rideEntity.getLocation());
        } else {
            rideResourceProperty.setTs(rideEntity.getTsMapMatched());
            rideResource.setGeometry(rideEntity.getLocationMapMatched());
        }

        rideResource.setProperties(rideResourceProperty);

        return rideResource;
    }
}
