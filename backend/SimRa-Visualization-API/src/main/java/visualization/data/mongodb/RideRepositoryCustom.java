package visualization.data.mongodb;

import org.springframework.data.mongodb.core.geo.GeoJsonPolygon;
import visualization.data.mongodb.entities.RideEntity;

import java.util.List;

public interface RideRepositoryCustom {

    List<RideEntity> findByPolygonIntersects(GeoJsonPolygon polygon);
    List<RideEntity> findFilteredRides(GeoJsonPolygon polygon, Long fromTs, Long untilTs, Integer fromMinutesOfDay, Integer untilMinutesOfDay, List<String> weekdays);

}
