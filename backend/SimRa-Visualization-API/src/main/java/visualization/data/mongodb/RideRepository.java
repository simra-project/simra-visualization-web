package visualization.data.mongodb;

import org.springframework.data.geo.Box;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.core.geo.GeoJsonPolygon;
import org.springframework.data.mongodb.repository.MongoRepository;
import visualization.data.mongodb.entities.RideEntity;

import java.util.List;
import java.util.Optional;

public interface RideRepository extends MongoRepository<RideEntity, String> {

    List<RideEntity> findByLocationNear(GeoJsonPoint coordinates, int maxDistance);

    List<RideEntity> findByLocationWithin(GeoJsonPolygon polygon);

    Optional<RideEntity> findById(String id);

    Optional<List<RideEntity>> findAllByTsBetween(Long fromTs, Long untilTs);
}
