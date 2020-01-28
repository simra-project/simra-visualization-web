package com.simra.app.csvimporter.repository;

import com.simra.app.csvimporter.model.LegEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface LegRepository extends MongoRepository<LegEntity, String>, LegRepositoryCustom {

}
