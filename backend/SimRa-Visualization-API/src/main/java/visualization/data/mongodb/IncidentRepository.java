package visualization.data.mongodb;

import com.mongodb.client.model.geojson.Point;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.repository.MongoRepository;
import visualization.data.mongodb.entities.IncidentEntity;

import java.util.List;
import java.util.Optional;


public interface IncidentRepository extends MongoRepository<IncidentEntity, IncidentEntity.CompositeKey> {
    //DB Interface for Incident data
    //TODO: Define proper DB Schema
    //TODO: Test queries with real data

    List<IncidentEntity> findByRideId(String rideId);
    Optional<IncidentEntity> findById(IncidentEntity.CompositeKey id);
    List<IncidentEntity> findByLocationNear(GeoJsonPoint coordinates, int maxDistance);

    /* reference:
        https://docs.mongodb.com/manual/geospatial-queries/
        https://docs.spring.io/spring-data/data-document/docs/current/reference/html/
     */

}
