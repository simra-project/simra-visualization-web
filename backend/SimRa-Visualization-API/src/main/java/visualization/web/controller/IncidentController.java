package visualization.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import visualization.service.IncidentService;
import visualization.web.resources.IncidentResource;
import visualization.web.resources.RideResource;

import java.util.List;


/*

This is the place where we communicate with the frontend

 */

@RestController
public class IncidentController {

    @Autowired
    private IncidentService incidentService;

    @GetMapping(value = "/incident")
    public HttpEntity<List<IncidentResource>> getIncidents(@RequestParam(value = "rideid") int rideId) {
        return ResponseEntity.ok(incidentService.getIncidentsByRideId(rideId));
    }

}
