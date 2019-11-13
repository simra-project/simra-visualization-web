package main.java.com.simra.app.csvimporter.model;

import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

/**
 * The type Ride.
 */
public class Ride implements MongoDocument {

    private List rideBeans;
    private List incidents;

    /**
     * Instantiates a new Ride.
     */
    public Ride() {
        // default constructor
    }

    /**
     * Instantiates a new Ride.
     *
     * @param rideBeans the ride beans
     * @param incidents the incidents
     */
    public Ride(List<RideCSV> rideBeans, List<IncidentCSV> incidents) {
        if (!rideBeans.isEmpty() && !incidents.isEmpty()){
            this.rideBeans = rideBeans;
            this.incidents = incidents;

        }
    }

    /**
     * Gets ride beans.
     *
     * @return the ride beans
     */
    public List getRideBeans() {
        return rideBeans;
    }

    /**
     * Sets ride beans.
     *
     * @param rideBeans the ride beans
     */
    public void setRideBeans(List rideBeans) {
        this.rideBeans = rideBeans;
    }

    /**
     * Gets incidents.
     *
     * @return the incidents
     */
    public List getIncidents() {
        return incidents;
    }

    /**
     * Sets incidents.
     *
     * @param incidents the incidents
     */
    public void setIncidents(List incidents) {
        this.incidents = incidents;
    }

    /**
     * To document object document.
     *
     * @return the document
     */
    public Document toDocumentObject(){
        ArrayList rides = new ArrayList<Document>();
        this.rideBeans.forEach(ride-> rides.add(((RideCSV)ride).toDocumentObject()));

        ArrayList incidents = new ArrayList<Document>();
        this.incidents.forEach(incident-> incidents.add(((IncidentCSV)incident).toDocumentObject()));
        Document singleRide= new Document();
        singleRide.put("rides",rides);
        singleRide.put("incidents",incidents);
        return singleRide;
    }

}
