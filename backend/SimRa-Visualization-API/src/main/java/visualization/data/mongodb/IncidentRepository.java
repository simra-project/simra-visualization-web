package visualization.data.mongodb;

import org.springframework.data.mongodb.repository.MongoRepository;
import visualization.data.mongodb.entities.IncidentEntity;
import visualization.web.resources.geoJSON.Point;

import java.util.List;


public interface IncidentRepository extends MongoRepository<IncidentEntity, String> {
    //DB Interface for Incident data
    //TODO: Define proper DB Schema
    //TODO: Test queries with real data

    List<IncidentEntity> findByRideId(int rideId);
    IncidentEntity findById(int rideIdKey);
    List<IncidentEntity> findByLocationNear(Point location, int minDistance, int maxDinstance);

    /* reference:
        https://docs.mongodb.com/manual/geospatial-queries/
        https://docs.spring.io/spring-data/data-document/docs/current/reference/html/
     */

}
