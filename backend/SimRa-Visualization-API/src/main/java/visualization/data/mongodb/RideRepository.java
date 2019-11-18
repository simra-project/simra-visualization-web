package visualization.data.mongodb;

import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.repository.MongoRepository;
import visualization.data.mongodb.entities.RideEntity;

import java.util.List;


public interface RideRepository extends MongoRepository<RideEntity, String> {

//DB Interface for Ride data
    List<RideEntity> findByLocationNear(GeoJsonPoint coordinates, int maxDistance);

}
