package main.java.com.simra.app.csvimporter.model;


public class Profile extends ProfileCSV {
    private String appVersion;
    private int fileVersion;

    public Profile(){
        //default constructor
    }

    public String getAppVersion() {
        return appVersion;
    }

    public void setAppVersion(String appVersion) {
        this.appVersion = appVersion;
    }

    public int getFileVersion() {
        return fileVersion;
    }

    public void setFileVersion(int fileVersion) {
        this.fileVersion = fileVersion;
    }

    @Override
    public String toString() {
        return String.format("[appVersion:%s, fileVersion=%s, CSV: %s ]",
                this.appVersion, this.fileVersion, super.toString());
    }
}
