package com.simra.app.csvimporter.repository;

import com.simra.app.csvimporter.model.CSVFile;
import org.springframework.data.mongodb.repository.MongoRepository;


public interface CSVFileRepository extends MongoRepository<CSVFile, String> {

    public CSVFile findByFileId(String fileId);

}
