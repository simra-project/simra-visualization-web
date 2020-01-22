package com.simra.app.csvimporter.repository;

import com.simra.app.csvimporter.model.RideEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface RideRepository extends MongoRepository<RideEntity, String> {

    RideEntity findByFileId(String fileId);

}
