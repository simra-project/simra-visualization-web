package com.simra.app.csvimporter.repository;

import com.simra.app.csvimporter.model.LegEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;

public class LegRepositoryCustomImpl implements LegRepositoryCustom {

    @Autowired
    private MongoTemplate mongotemplate;

    @Override
    public List<LegEntity> findByGeometryIntersection(LegEntity legEntity) {
        Query query = new Query();

        query.addCriteria(Criteria.where("geometry").intersects(legEntity.getGeometry()));
        return mongotemplate.find(query, LegEntity.class);

    }
}
