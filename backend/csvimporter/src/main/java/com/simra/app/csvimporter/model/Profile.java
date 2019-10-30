package main.java.com.simra.app.csvimporter.model;

import java.util.Arrays;

public class Profile {
    private String appVersion;
    private int fileVersion;
    private int birth;
    private int gender;
    private int region;
    private int experience;
    private int numberOfRides;
    private long duration;
    private int numberOfIncidents;
    private long length; // distance
    private int idle; //waitedTime
    private int numberOfScary;
    private int[] hour;
    private int behaviour;

    public Profile(){
        this.hour = new int[24];
        Arrays.fill(this.hour,0);
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

    public int getBirth() {
        return birth;
    }

    public void setBirth(int birth) {
        this.birth = birth;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public int getRegion() {
        return region;
    }

    public void setRegion(int region) {
        this.region = region;
    }

    public int getExperience() {
        return experience;
    }

    public void setExperience(int experience) {
        this.experience = experience;
    }

    public int getNumberOfRides() {
        return numberOfRides;
    }

    public void setNumberOfRides(int numberOfRides) {
        this.numberOfRides = numberOfRides;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public int getNumberOfIncidents() {
        return numberOfIncidents;
    }

    public void setNumberOfIncidents(int numberOfIncidents) {
        this.numberOfIncidents = numberOfIncidents;
    }

    public long getLength() {
        return length;
    }

    public void setLength(long length) {
        this.length = length;
    }

    public int getIdle() {
        return idle;
    }

    public void setIdle(int idle) {
        this.idle = idle;
    }

    public int getNumberOfScary() {
        return numberOfScary;
    }

    public void setNumberOfScary(int numberOfScary) {
        this.numberOfScary = numberOfScary;
    }

    public int[] getHour() {
        return hour;
    }

    public void setHour(int[] hour) {
        this.hour = hour;
    }

    public int getBehaviour() {
        return behaviour;
    }

    public void setBehaviour(int behaviour) {
        this.behaviour = behaviour;
    }
}
