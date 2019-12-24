package visualization.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.core.geo.GeoJsonPolygon;
import org.springframework.stereotype.Service;
import visualization.data.mongodb.LegRepository;
import visualization.data.mongodb.entities.LegEntity;
import visualization.web.resources.LegResource;
import visualization.web.resources.serializers.LegResourceMapper;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class LegServiceImpl implements LegService {

    @Autowired
    private LegRepository legRepository;

    @Autowired
    private LegResourceMapper legResourceMapper;

    @Override
    public List<LegResource> getLegsWithin(GeoJsonPoint first, GeoJsonPoint second, GeoJsonPoint third, GeoJsonPoint fourth, Integer minWeight) {
        GeoJsonPolygon polygon = new GeoJsonPolygon(first, second, third, fourth, first);
        List<LegEntity> legEntities = legRepository.findByGeometryWithin(polygon, minWeight);

        return legEntities.stream().map(legEntity -> legResourceMapper.mapLegEntityToResource(legEntity)).collect(Collectors.toList());
    }
}
