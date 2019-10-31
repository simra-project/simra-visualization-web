package main.java.com.simra.app.csvimporter.model;

/**
 * The type Application  and file version.
 * file id is the name of the file.
 */
public abstract class ApplicationFileVersion {

    private String appVersion;
    private int fileVersion;
    private String fileId;

    /**
     * Gets app version.
     *
     * @return the app version
     */
    String getAppVersion() {
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
    int getFileVersion() {
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
    String getFileId() {
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
