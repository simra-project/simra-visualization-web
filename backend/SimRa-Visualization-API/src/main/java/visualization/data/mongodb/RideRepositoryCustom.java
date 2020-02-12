package visualization.data.mongodb;

import org.springframework.data.mongodb.core.geo.GeoJsonPolygon;
import visualization.data.mongodb.entities.IncidentEntity;
import visualization.data.mongodb.entities.RideEntity;

import java.util.List;

public interface RideRepositoryCustom {

    List<RideEntity> findFilteredRides(GeoJsonPolygon polygon, Integer fromMinutesOfDay, Integer untilMinutesOfDay, String weekday);

    List<RideEntity> findByPolygonIntersects(GeoJsonPolygon polygon);

}
