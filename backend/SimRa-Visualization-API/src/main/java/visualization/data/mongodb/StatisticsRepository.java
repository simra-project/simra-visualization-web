package visualization.data.mongodb;

import org.springframework.data.mongodb.repository.MongoRepository;
import visualization.data.mongodb.entities.IncidentEntity;
import visualization.web.resources.geoJSON.Point;

import java.util.List;


public interface StatisticsRepository extends MongoRepository<IncidentEntity, String> {

//DB Interface for Statistics data

}
