package com.simra.app.csvimporter.controller;

import com.simra.app.csvimporter.model.CSVFile;
import org.springframework.data.mongodb.repository.MongoRepository;


public interface CSVFileRepository extends MongoRepository<CSVFile, String> {

    CSVFile findByFileId(String fileId);

}
