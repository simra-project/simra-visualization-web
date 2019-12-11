package com.simra.app.csvimporter.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "profile")
public class ProfileEntity extends ProfileCSV {

    @Id
    public String id;

    public String directoryRegion;

    public ProfileEntity() {
        super();
    }

    public ProfileEntity(String fileId) {
        this.setFileId(fileId);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDirectoryRegion() {
        return directoryRegion;
    }

    public void setDirectoryRegion(String directoryRegion) {
        this.directoryRegion = directoryRegion;
    }

    public String toString() {
        return String.format(" [id:%s] ", this.id);
    }

}
