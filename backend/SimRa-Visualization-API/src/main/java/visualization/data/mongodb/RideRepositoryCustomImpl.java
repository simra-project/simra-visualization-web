package visualization.data.mongodb;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.geo.GeoJsonPolygon;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import visualization.data.mongodb.entities.IncidentEntity;
import visualization.data.mongodb.entities.RideEntity;

import java.util.List;

public class RideRepositoryCustomImpl implements RideRepositoryCustom {

    @Autowired
    MongoOperations operations;

    @Autowired
    private MongoTemplate mongotemplate;

    @Override
    public List<RideEntity> findByPolygonIntersects(GeoJsonPolygon polygon) {
        Document geoJsonDbo = new Document();
        operations.getConverter().write(polygon, geoJsonDbo);
        BasicQuery basicQuery = new BasicQuery(new Document("location", new Document("$geoIntersects", new Document("$geometry", geoJsonDbo))));
        System.out.println(basicQuery);
        return operations.find(basicQuery, RideEntity.class);
    }

    @Override
    public List<RideEntity> findFilteredRides(GeoJsonPolygon polygon, Integer fromMinutesOfDay, Integer untilMinutesOfDay, String weekday) {

        Query query = new Query();
        if (polygon != null) {
            query.addCriteria(Criteria.where("location").within(polygon));
        }
        if (fromMinutesOfDay != null && untilMinutesOfDay != null) {
            query.addCriteria(Criteria.where("minuteOfDay").gte(fromMinutesOfDay).lte(untilMinutesOfDay));
        }
        if (weekday != null) {
            query.addCriteria(Criteria.where("weekday").is(weekday));
        }

        return mongotemplate.find(query, RideEntity.class);
    }

}
