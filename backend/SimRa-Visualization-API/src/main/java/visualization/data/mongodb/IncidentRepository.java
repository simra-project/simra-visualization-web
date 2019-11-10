package visualization.data.mongodb;

import org.springframework.data.mongodb.repository.MongoRepository;
import visualization.data.mongodb.entities.IncidentEntity;

import java.util.List;


public interface IncidentRepository extends MongoRepository<IncidentEntity, String> {
    List<IncidentEntity> findByRideId(int rideId);

}
