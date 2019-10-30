package main.java.com.simra.app.csvimporter.model;

import com.opencsv.bean.CsvBindAndJoinByName;
import com.opencsv.bean.CsvBindByName;
import org.apache.commons.collections4.MultiValuedMap;


public class ProfileCSV {

    @CsvBindByName(column = "birth")
    private int birth;

    @CsvBindByName(column = "gender")
    private int gender;

    @CsvBindByName(column = "region")
    private int region;

    @CsvBindByName(column = "experience")
    private int experience;

    @CsvBindByName(column = "numberOfRides")
    private int numberOfRides;

    @CsvBindByName(column = "duration")
    private long duration;

    @CsvBindByName(column = "numberOfIncidents")
    private int numberOfIncidents;

    @CsvBindByName(column = "waitedTime")
    private int idle; //waitedTime

    @CsvBindByName(column = "distance")
    private float distance; // length

    @CsvBindByName(column = "length")
    private float length; // old attribute name

    @CsvBindByName(column = "co2")
    private int co2; //waitedTime

    @CsvBindAndJoinByName(column = "[0-9]+", elementType = String.class)
    private MultiValuedMap<String, Float> hours;

    @CsvBindByName(column = "behaviour")
    private int behaviour;

    @CsvBindByName(column = "numberOfScary")
    private int numberOfScary;

    public MultiValuedMap<String, Float> getHours() {
        return hours;
    }

    public void setHours(MultiValuedMap<String, Float> hours) {
        this.hours = hours;
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

    public float getDistance() {
        return distance;
    }

    public void setDistance(float distance) {
        this.distance = distance;
    }

    public float getLength() {
        return this.length;
    }

    public void setLength(float length) {
        this.length = length;
        this.distance = length;
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

    public int getBehaviour() {
        return behaviour;
    }

    public void setBehaviour(int behaviour) {
        this.behaviour = behaviour;
    }

    public int getCo2() {
        return co2;
    }

    public void setCo2(int co2) {
        this.co2 = co2;
    }


    public String toString() {

        return String.format(" [birth:%s, gender:%s, region: %s, " +
                        "numberOfRides: %s, duration: %s, numberOfIncidents: %s, distance: %s, co2: %s," +
                        "idle: %s, numberOfScary: %s, behaviour: %s, hours: %s] ",
                this.birth, this.gender, this.region, this.numberOfRides,
                this.duration, this.numberOfIncidents, this.distance, this.co2, this.idle, this.numberOfScary, this.behaviour, this.hours.toString());
    }
}
