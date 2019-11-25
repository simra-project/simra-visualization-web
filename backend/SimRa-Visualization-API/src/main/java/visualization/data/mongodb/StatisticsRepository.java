package visualization.data.mongodb;

import org.springframework.data.mongodb.repository.MongoRepository;
import visualization.data.mongodb.entities.IncidentEntity;


public interface StatisticsRepository extends MongoRepository<IncidentEntity, String> {

//DB Interface for Statistics data

}
