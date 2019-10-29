package main.java.com.simra.app.csvimporter.model;

import java.util.Arrays;

public class Profile {
    String appVersion;
    int fileVersion;
    int birth;
    int gender;
    int region;
    int experience;
    int numberOfRides;
    long duration;
    int numberOfIncidents;
    long length; // distance
    int idle; //waitedTime
    int numberOfScary;
    private int[] hour;
    int behaviour;

    public Profile(){
        this.hour = new int[24];
        Arrays.fill(this.hour,0);
    }

}
