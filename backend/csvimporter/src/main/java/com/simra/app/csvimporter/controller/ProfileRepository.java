package com.simra.app.csvimporter.controller;

import com.simra.app.csvimporter.model.ProfileEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ProfileRepository extends MongoRepository<ProfileEntity, String> {

    ProfileEntity findByFileId(String fileId);

}
