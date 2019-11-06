package main.java.com.simra.app.csvimporter.model;

import java.util.List;

public class Ride {

    private List rideBeans;
    private List incidents;

    public Ride(){
        // default constructor
    }

    public Ride(List<RideCSV> rideBeans, List<IncidentCSV> incidents){
        this.rideBeans=rideBeans;
        this.incidents= incidents;
    }

    public List getRideBeans() {
        return rideBeans;
    }

    public void setRideBeans(List rideBeans) {
        this.rideBeans = rideBeans;
    }

    public List getIncidents() {
        return incidents;
    }

    public void setIncidents(List incidents) {
        this.incidents = incidents;
    }
}
