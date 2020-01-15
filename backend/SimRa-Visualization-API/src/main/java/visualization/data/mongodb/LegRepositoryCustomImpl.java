package visualization.data.mongodb;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.geo.GeoJsonPolygon;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import visualization.data.mongodb.entities.LegEntity;

import java.util.List;

public class LegRepositoryCustomImpl implements LegRepositoryCustom {

    @Autowired
    private MongoTemplate mongotemplate;

    @Override
    public List<LegEntity> findByGeometryWithin(GeoJsonPolygon polygon, Integer minWeight) {
        Query query = new Query();
        query.addCriteria(Criteria.where("geometry").within(polygon).and("properties.fileIdSetCount").gte(minWeight));
        return mongotemplate.find(query, LegEntity.class);
    }
}
