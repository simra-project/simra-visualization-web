package main.java.com.simra.app.csvimporter.model;

import com.mongodb.client.model.geojson.MultiPoint;
import com.mongodb.client.model.geojson.Position;
import org.bson.Document;

import java.util.ArrayList;
import java.util.Arrays;
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
        if (!rideBeans.isEmpty() && !incidents.isEmpty()) {
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
    @Override
    public Document toDocumentObject() {
       Document singleRide = new Document();
        singleRide.put("rideId", ((RideCSV) this.getRideBeans().get(0)).getFileId());
        ArrayList<Position> coordinates= new ArrayList<>();

        this.rideBeans.forEach(ride -> {
            RideCSV rideCSV= (RideCSV) ride;
            List<Double> places = Arrays.asList(Double.parseDouble(rideCSV.getA()), Double.parseDouble(rideCSV.getLon()));
            Position pos= new Position(places);
            coordinates.add(pos);
        });
        MultiPoint coordinates_multi= new MultiPoint(coordinates);

        singleRide.put("location", coordinates_multi);
        ArrayList ts = new ArrayList<String>();
        this.rideBeans.forEach(ride -> ts.add(((RideCSV) ride).getTimeStamp()));
        singleRide.put("ts", ts);

        return singleRide;
    }

    public List<Document> incidentsDocuments() {
        ArrayList incidentsList = new ArrayList<Document>();
        this.incidents.forEach(incident -> incidentsList.add(((IncidentCSV) incident).toDocumentObject()));
        return incidentsList;
    }
}
