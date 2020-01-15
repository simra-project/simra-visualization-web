package visualization.data.mongodb;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.geo.GeoJsonPolygon;
import org.springframework.data.mongodb.core.query.BasicQuery;
import visualization.data.mongodb.entities.RideEntity;

import java.util.List;

public class RideRepositoryCustomImpl implements RideRepositoryCustom {

    @Autowired
    MongoOperations operations;

    @Override
    public List<RideEntity> findByPolygonIntersects(GeoJsonPolygon polygon) {
        Document geoJsonDbo = new Document();
        operations.getConverter().write(polygon, geoJsonDbo);
        BasicQuery basicQuery = new BasicQuery(new Document("location", new Document("$geoIntersects", new Document("$geometry", geoJsonDbo))));
        System.out.println(basicQuery);
        return operations.find(basicQuery, RideEntity.class);
    }

}
