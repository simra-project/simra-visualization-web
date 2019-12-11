package com.simra.app.csvimporter.controller;

import com.simra.app.csvimporter.model.IncidentEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface IncidentRepository extends MongoRepository<IncidentEntity, String> {

    public IncidentEntity findByFileId(String fileId);

}
