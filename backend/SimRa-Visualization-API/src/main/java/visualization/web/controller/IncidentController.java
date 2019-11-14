package visualization.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import visualization.service.IncidentService;
import visualization.web.resources.IncidentResource;

import java.util.List;


/*

This is the place where we communicate with the frontend regarding Incident Queries

 */

@RestController
public class IncidentController {

    @Autowired
    private IncidentService incidentService;

    // get exactly one incident by rideId and adding the ?key=XY as request parameter
    @GetMapping(value = "/rides/{rideId}/incidents/{key}")
    public HttpEntity<IncidentResource> getIncident(@PathVariable int rideId,
                                                    @PathVariable int key) {
        return ResponseEntity.ok(incidentService.getIncident(rideId, key));
    }

    // get all incidents of one ride by id
    @GetMapping(value = "/rides/{rideId}/incidents/all")
    public HttpEntity<List<IncidentResource>> getRideIncidents(@PathVariable String rideId) {
        return ResponseEntity.ok(incidentService.getIncidentsByRideId(rideId));
    }

    // get all incidents in range minDistance and maxDistance around a Point (lat, long)
    @GetMapping(value = "/incidents")
    public HttpEntity<List<IncidentResource>> getIncidentsNear(@RequestParam(value = "lat") double latitude,
                                                               @RequestParam(value = "long") double longitude,
                                                               @RequestParam(value = "min") int minDistance,
                                                               @RequestParam(value = "max") int maxDistance) {
        return ResponseEntity.ok(incidentService.getIncidentsInRange(latitude, longitude, minDistance, maxDistance));
    }

}
