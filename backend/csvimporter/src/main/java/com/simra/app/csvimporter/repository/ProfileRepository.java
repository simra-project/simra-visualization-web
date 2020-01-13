package com.simra.app.csvimporter.repository;

import com.simra.app.csvimporter.model.ProfileEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ProfileRepository extends MongoRepository<ProfileEntity, String> {

    public ProfileEntity findByFileId(String fileId);

}
