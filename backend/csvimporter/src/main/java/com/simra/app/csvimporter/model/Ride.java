package main.java.com.simra.app.csvimporter.model;

import com.mongodb.client.model.geojson.LineString;
import com.mongodb.client.model.geojson.Position;
import org.apache.log4j.Logger;
import org.bson.Document;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;


/**
 * The type Ride.
 */
public class Ride implements MongoDocument {
    private static final Logger logger = Logger.getLogger(Ride.class);


    private List rideBeans;
    private List mapMatchedRideBeans;
    private List incidents;

    /**
     * Instantiates a new Ride.
     */
    public Ride() {
        // default constructor
    }

    /**
     * Gets ride beans.
     *
     * @return the ride beans
     */
    public List getRideBeans() {
        return this.rideBeans;
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
     * Gets map matched ride beans.
     *
     * @return the map matched ride beans
     */
    public List getMapMatchedRideBeans() {
        return this.mapMatchedRideBeans;
    }

    /**
     * Sets map matched ride beans.
     *
     * @param mapMatchedRideBeans the map matched ride beans
     */
    public void setMapMatchedRideBeans(List mapMatchedRideBeans) {
        this.mapMatchedRideBeans = mapMatchedRideBeans;
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

        parseRideBeans(singleRide, rideBeans, "");
        parseRideBeans(singleRide, mapMatchedRideBeans, "MapMatched");
        singleRide.put("importedAt", new Date());
        return singleRide;
    }

    private void parseRideBeans(Document document, List<RideCSV> rideBeans, String suffix) {
        ArrayList<Position> coordinates = new ArrayList<>();

        try {


            rideBeans.forEach(ride -> {
                List<Double> places = Arrays.asList(Double.parseDouble(ride.getLon()), Double.parseDouble(ride.getLat()));
                Position pos = new Position(places);
                coordinates.add(pos);
            });
            LineString coordinatesMulti = new LineString(coordinates);

            document.put("location" + suffix, coordinatesMulti);
            ArrayList ts = new ArrayList<String>();
            rideBeans.forEach(ride -> ts.add((ride).getTimeStamp()));
            document.put("ts" + suffix, ts);
        }catch (NullPointerException e){
            logger.error(e);
        }
    }

    /**
     * Incidents documents list.
     *
     * @return the list
     */
    public List<Document> incidentsDocuments() {
        ArrayList incidentsList = new ArrayList<Document>();
        this.incidents.forEach(incident -> incidentsList.add(((IncidentCSV) incident).toDocumentObject()));
        return incidentsList;
    }
}
