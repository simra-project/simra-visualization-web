package com.simra.app.csvimporter.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "csv")
public class CSVFile {

    @Id
    public String id;

    public String fileId;

    public String type;

    public CSVFile() {
    }

    public CSVFile(String fileId) {
        this.fileId = fileId;
    }

    public CSVFile(String fileId, String type) {
        this.fileId = fileId;
        this.type = type;
    }

    @Override
    public String toString() {
        return String.format("CSVFile[id=%s, type=%s]", fileId, type);
    }

}