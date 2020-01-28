package com.simra.app.csvimporter.repository;

import com.simra.app.csvimporter.model.IncidentEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface IncidentRepository extends MongoRepository<IncidentEntity, String> {

    IncidentEntity findByFileId(String fileId);

}
