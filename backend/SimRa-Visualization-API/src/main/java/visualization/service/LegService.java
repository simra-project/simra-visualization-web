package visualization.service;

import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import visualization.web.resources.LegResource;

import java.util.List;

public interface LegService {
    List<LegResource> getLegsWithin(GeoJsonPoint first, GeoJsonPoint second, GeoJsonPoint third, GeoJsonPoint fourth, Integer minWeight, String day, Double minDist, Double maxDist);
}
