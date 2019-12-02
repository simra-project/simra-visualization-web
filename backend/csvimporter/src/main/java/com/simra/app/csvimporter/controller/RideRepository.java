package com.simra.app.csvimporter.controller;

import com.simra.app.csvimporter.model.RideEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface RideRepository extends MongoRepository<RideEntity, String> {

    public RideEntity findByFileId(String fileId);

}
