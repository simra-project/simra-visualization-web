package visualization.data.mongodb;

import org.springframework.stereotype.Repository;
import visualization.web.resources.IncidentResource;
import visualization.web.resources.RideResource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/*

This is the place where we have to implement the database access and the queries
Values from the database must be mapped to the Resources, e.g. RideResource accordingly

 */

@Repository
public class IncidentRepositoryImpl implements IncidentRepository {

    // Dummy Method for sample data
    @Override
    public RideResource getAllRideIncidents() {

        HashMap<String, Double> map = new HashMap();
        map.put("lat",52.512830);
        map.put("long",13.322887);

        IncidentResource incidentResource = new IncidentResource(1,map, "Stau in der Mensa");
        IncidentResource incidentResource2 = new IncidentResource(2,map, "MongoDB");
        IncidentResource incidentResource3 = new IncidentResource(3,map, "Stau in der Mensa");
        List<IncidentResource> incidents = new ArrayList<>();
        incidents.add(incidentResource);
        incidents.add(incidentResource2);
        incidents.add(incidentResource3);

        RideResource ride = new RideResource(1, incidents);
        //String incident = "{\n            id: \"Incident 1\",\n            latlng: {\n                lat: 52.512830,\n                lng: 13.322887\n            },\n            description: \"Auto hat mich beim Einfaedeln fast mitgenommen!\"\n        },\n        {\n            id: \"Incident 2\",\n            latlng: {\n                lat: 52.512719,\n                lng: 13.324711\n            },\n            description: \"Wurde von ein paar Vertretern auf ein Jobangebot angesprochen...\"\n        },\n        {\n            id: \"Incident 3\",\n            latlng: {\n                lat: 52.509777,\n                lng: 13.325281\n            },\n            description: \"Viel zu lange Schlangen in der Mensa.\"\n        }";

        return ride;
    }


    @Override
    public RideResource getRideIncidentsById(int rideId) {

        //Use rideId to filter by Ride in Query
        //Database Query go here
        return null;
    }

}
