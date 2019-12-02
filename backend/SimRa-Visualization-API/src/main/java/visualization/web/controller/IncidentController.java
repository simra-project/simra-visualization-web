package visualization.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import visualization.service.IncidentService;
import visualization.web.resources.IncidentResource;

import java.util.List;


/*

This is the place where we communicate with the frontend regarding Incident Queries

 */

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
public class IncidentController {

    @Autowired
    private IncidentService incidentService;

    // get exactly one incident by rideId and adding the ?key=[0-N] as request parameter
    @GetMapping(value = "/rides/{rideId}/incidents/{key}")
    public HttpEntity<IncidentResource> getIncident(@PathVariable String rideId,
                                                    @PathVariable String key) {
        return ResponseEntity.ok(incidentService.getIncident(rideId, key));
    }

    // get all incidents of one ride by id
    @GetMapping(value = "/rides/{rideId}/incidents/all")
    public HttpEntity<List<IncidentResource>> getRideIncidents(@PathVariable String rideId) {
        return ResponseEntity.ok(incidentService.getIncidentsByRideId(rideId));
    }

    // get all incidents in range minDistance and maxDistance around a Point (longitude, latitude)
    @GetMapping(value = "/incidents")
    public HttpEntity<List<IncidentResource>> getIncidentsNear(@RequestParam(value = "lon") double longitude,
                                                               @RequestParam(value = "lat") double latitude,
                                                               @RequestParam(value = "max") int maxDistance) {
        return ResponseEntity.ok(incidentService.getIncidentsInRange(longitude, latitude, maxDistance));
    }

    //example: http://localhost:8080/incidents/area?first=13.297089,52.481744&second=13.448689,52.509574&third=13.456360,52.547463&fourth=13.305468, 52.546459
    @GetMapping(value = "/incidents/area")
    public HttpEntity<List<IncidentResource>> getIncidentsWithin(@RequestParam(value = "first") double[] first,
                                                                 @RequestParam(value = "second") double[] second,
                                                                 @RequestParam(value = "third") double[] third,
                                                                 @RequestParam(value = "fourth") double[] fourth) {
        return ResponseEntity.ok(incidentService.getIncidentsInWithin(new GeoJsonPoint(first[0], first[1]),
                new GeoJsonPoint(second[0], second[1]),
                new GeoJsonPoint(third[0], third[1]),
                new GeoJsonPoint(fourth[0], fourth[1])));
    }

}
