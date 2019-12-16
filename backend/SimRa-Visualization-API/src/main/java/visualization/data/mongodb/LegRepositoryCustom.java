package visualization.data.mongodb;

import org.springframework.data.mongodb.core.geo.GeoJsonPolygon;
import visualization.data.mongodb.entities.LegEntity;

import java.util.List;

public interface LegRepositoryCustom {
    List<LegEntity> findByGeometryWithin(GeoJsonPolygon polygon, Integer minWeight);
}
