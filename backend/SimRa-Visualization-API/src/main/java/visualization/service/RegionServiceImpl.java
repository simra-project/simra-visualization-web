package visualization.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import visualization.data.mongodb.RegionRepository;
import visualization.data.mongodb.entities.RegionEntity;
import visualization.web.resources.RegionResource;
import visualization.web.resources.serializers.RegionResourceMapper;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RegionServiceImpl implements RegionService {

    @Autowired
    private RegionRepository regionRepository;

    @Autowired
    private RegionResourceMapper regionResourceMapper;

    @Override
    public List<RegionResource> getRegions() {
        List<RegionEntity> regionEntities = regionRepository.findAll();
        return regionEntities.stream().map(regionEntity -> regionResourceMapper.mapRegionToResource(regionEntity)).collect(Collectors.toList());
    }

    @Override
    public RegionResource createRegion(RegionEntity regionEntity) {
        return regionResourceMapper.mapRegionToResource(regionRepository.save(regionEntity));
    }

    @Override
    public void deleteRegion(String regionId) {
        regionRepository.deleteById(regionId);
    }


}
