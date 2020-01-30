package visualization.web.resources.serializers;

import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.stereotype.Component;
import visualization.data.mongodb.entities.RegionEntity;
import visualization.web.resources.RegionResource;
import visualization.web.resources.RegionResourceProperty;

@Component
public class RegionResourceMapper {

    public RegionResource mapRegionToResource(RegionEntity regionEntity) {

        RegionResource regionResource = new RegionResource();
        RegionResourceProperty regionResourceProperty = new RegionResourceProperty();

        regionResourceProperty.setName(regionEntity.getName());
        regionResourceProperty.setDefaultZoomLevel(regionEntity.getDefaultZoomLevel());
        regionResourceProperty.setMaxZoomLevel(regionEntity.getMaxZoomLevel());
        regionResourceProperty.setBottomLeftBound(regionEntity.getBottomLeftBound());
        regionResourceProperty.setTopRightBound(regionEntity.getTopRightBound());
        regionResourceProperty.setDescription(regionEntity.getDescription());

//        regionResource.setGeometry(new GeoJsonPoint(regionEntity.getLocation()[0], regionEntity.getLocation()[1]));
        regionResource.setGeometry(new GeoJsonPoint(regionEntity.getLocation()));
        regionResource.setProperties(regionResourceProperty);

        return regionResource;
    }
}
