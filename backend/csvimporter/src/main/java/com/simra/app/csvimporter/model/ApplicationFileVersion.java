package com.simra.app.csvimporter.model;

import org.springframework.data.mongodb.core.index.Indexed;

/**
 * The type Application  and file version.
 * file id is the name of the file.
 */
public abstract class ApplicationFileVersion {

    public String appVersion;

    public int fileVersion;

    @Indexed
    public String fileId;

    /**
     * Gets app version.
     *
     * @return the app version
     */
    public String getAppVersion() {
        return appVersion;
    }

    /**
     * Sets app version.
     *
     * @param appVersion the app version
     */
    public void setAppVersion(String appVersion) {
        this.appVersion = appVersion;
    }

    /**
     * Gets file version.
     *
     * @return the file version
     */
    public int getFileVersion() {
        return fileVersion;
    }

    /**
     * Sets file version.
     *
     * @param fileVersion the file version
     */
    public void setFileVersion(int fileVersion) {
        this.fileVersion = fileVersion;
    }

    /**
     * Gets file id.
     *
     * @return the file id
     */
    public String getFileId() {
        return fileId;
    }

    /**
     * Sets file id.
     *
     * @param fileId the file id
     */
    public void setFileId(String fileId) {
        this.fileId = fileId;
    }
}
