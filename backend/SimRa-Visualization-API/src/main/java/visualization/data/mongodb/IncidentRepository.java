package visualization.data.mongodb;

import org.springframework.data.geo.Box;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.core.geo.GeoJsonPolygon;
import org.springframework.data.mongodb.repository.MongoRepository;
import visualization.data.mongodb.entities.IncidentEntity;

import java.util.List;
import java.util.Optional;

public interface IncidentRepository extends MongoRepository<IncidentEntity, IncidentEntity.CompositeKey>, IncidentRepositoryCustom {

    List<IncidentEntity> findByRideId(String rideId);

    Optional<IncidentEntity> findById(IncidentEntity.CompositeKey id);

    List<IncidentEntity> findByLocationNear(GeoJsonPoint coordinates, int maxDistance);
    //https://docs.mongodb.com/manual/reference/operator/query/box/

    List<IncidentEntity> findByLocationWithin(GeoJsonPolygon polygon);

}
