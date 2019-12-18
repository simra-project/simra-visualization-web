package com.simra.app.csvimporter.model;

import com.opencsv.bean.CsvBindAndJoinByName;
import com.opencsv.bean.CsvBindByName;
import org.apache.commons.collections4.MultiValuedMap;

/**
 * The Profile csv model.
 */
public class ProfileCSV extends ApplicationFileVersion {


    @CsvBindByName(column = "birth")
    public int birth;

    @CsvBindByName(column = "gender")
    public int gender;

    @CsvBindByName(column = "region")
    public int region;

    @CsvBindByName(column = "experience")
    public int experience;

    @CsvBindByName(column = "numberOfRides")
    public int numberOfRides;

    @CsvBindByName(column = "duration")
    public long duration;

    @CsvBindByName(column = "numberOfIncidents")
    public int numberOfIncidents;

    @CsvBindByName(column = "waitedTime")
    public int idle; //waitedTime

    @CsvBindByName(column = "distance")
    public float distance; // length

    @CsvBindByName(column = "length")
    public float length; // old attribute name

    @CsvBindByName(column = "co2")
    public int co2; //waitedTime

    @CsvBindAndJoinByName(column = "[0-9]+", elementType = String.class)
    public MultiValuedMap<String, Float> hours;

    @CsvBindByName(column = "behaviour")
    public int behaviour;

    @CsvBindByName(column = "numberOfScary")
    public int numberOfScary;

    /**
     * Gets hours.
     *
     * @return the hours
     */
    public MultiValuedMap<String, Float> getHours() {
        return hours;
    }

    /**
     * Sets hours.
     *
     * @param hours the hours
     */
    public void setHours(MultiValuedMap<String, Float> hours) {
        this.hours = hours;
    }

    /**
     * Gets birth.
     *
     * @return the birth
     */
    public int getBirth() {
        return birth;
    }

    /**
     * Sets birth.
     *
     * @param birth the birth
     */
    public void setBirth(int birth) {
        this.birth = birth;
    }

    /**
     * Gets gender.
     *
     * @return the gender
     */
    public int getGender() {
        return gender;
    }

    /**
     * Sets gender.
     *
     * @param gender the gender
     */
    public void setGender(int gender) {
        this.gender = gender;
    }

    /**
     * Gets region.
     *
     * @return the region
     */
    public int getRegion() {
        return region;
    }

    /**
     * Sets region.
     *
     * @param region the region
     */
    public void setRegion(int region) {
        this.region = region;
    }

    /**
     * Gets experience.
     *
     * @return the experience
     */
    public int getExperience() {
        return experience;
    }

    /**
     * Sets experience.
     *
     * @param experience the experience
     */
    public void setExperience(int experience) {
        this.experience = experience;
    }

    /**
     * Gets number of rides.
     *
     * @return the number of rides
     */
    public int getNumberOfRides() {
        return numberOfRides;
    }

    /**
     * Sets number of rides.
     *
     * @param numberOfRides the number of rides
     */
    public void setNumberOfRides(int numberOfRides) {
        this.numberOfRides = numberOfRides;
    }

    /**
     * Gets duration.
     *
     * @return the duration
     */
    public long getDuration() {
        return duration;
    }

    /**
     * Sets duration.
     *
     * @param duration the duration
     */
    public void setDuration(long duration) {
        this.duration = duration;
    }

    /**
     * Gets number of incidents.
     *
     * @return the number of incidents
     */
    public int getNumberOfIncidents() {
        return numberOfIncidents;
    }

    /**
     * Sets number of incidents.
     *
     * @param numberOfIncidents the number of incidents
     */
    public void setNumberOfIncidents(int numberOfIncidents) {
        this.numberOfIncidents = numberOfIncidents;
    }

    /**
     * Gets distance.
     *
     * @return the distance
     */
    public float getDistance() {
        return distance;
    }

    /**
     * Sets distance.
     *
     * @param distance the distance
     */
    public void setDistance(float distance) {
        this.distance = distance;
    }

    /**
     * Gets length.
     *
     * @return the length
     */
    public float getLength() {
        return this.length;
    }

    /**
     * Sets length.
     *
     * @param length the length
     */
    public void setLength(float length) {
        this.length = length;
        this.distance = length;
    }

    /**
     * Gets idle.
     *
     * @return the idle
     */
    public int getIdle() {
        return idle;
    }

    /**
     * Sets idle.
     *
     * @param idle the idle
     */
    public void setIdle(int idle) {
        this.idle = idle;
    }

    /**
     * Gets number of scary.
     *
     * @return the number of scary
     */
    public int getNumberOfScary() {
        return numberOfScary;
    }

    /**
     * Sets number of scary.
     *
     * @param numberOfScary the number of scary
     */
    public void setNumberOfScary(int numberOfScary) {
        this.numberOfScary = numberOfScary;
    }

    /**
     * Gets behaviour.
     *
     * @return the behaviour
     */
    public int getBehaviour() {
        return behaviour;
    }

    /**
     * Sets behaviour.
     *
     * @param behaviour the behaviour
     */
    public void setBehaviour(int behaviour) {
        this.behaviour = behaviour;
    }

    /**
     * Gets co 2.
     *
     * @return the co 2
     */
    public int getCo2() {
        return co2;
    }

    /**
     * Sets co 2.
     *
     * @param co2 the co 2
     */
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


