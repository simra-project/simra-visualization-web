package main.java.com.simra.app.csvimporter.model;


/**
 * The User Profile Model.
 */
public class Profile extends ProfileCSV {

    /**
     * Instantiates a new Profile.
     */
    public Profile() {
        super();
    }

    @Override
    public String toString() {
        return String.format("[fileID: %s, appVersion:%s, fileVersion=%s, CSV: %s ]",
                this.getFileId(), this.getAppVersion(), this.getFileVersion(), super.toString());
    }
}
