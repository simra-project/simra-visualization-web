package visualization.data.mongodb;

import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.core.geo.GeoJsonPolygon;
import org.springframework.data.mongodb.repository.MongoRepository;
import visualization.data.mongodb.entities.RideEntity;

import java.util.List;
import java.util.Optional;

public interface RideRepository extends MongoRepository<RideEntity, String>, RideRepositoryCustom {

    List<RideEntity> findByLocationNear(GeoJsonPoint coordinates, int maxDistance);

    List<RideEntity> findByLocationWithin(GeoJsonPolygon polygon);

    List<RideEntity> findByWeekday(String day);

    Optional<RideEntity> findById(String id);

    List<RideEntity> findByDistanceBetween(double min, double max);

    //@Query(sort = "{'ts': -1}")
    List<RideEntity> findAllByTsBetween(Long fromTs, Long untilTs);
}
