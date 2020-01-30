package visualization.service;

import visualization.data.mongodb.entities.RegionEntity;
import visualization.web.resources.RegionResource;

import java.util.List;

public interface RegionService {

    List<RegionResource> getRegions();

    RegionResource createRegion(RegionEntity regionEntity);

    void deleteRegion(String regionId);
}
