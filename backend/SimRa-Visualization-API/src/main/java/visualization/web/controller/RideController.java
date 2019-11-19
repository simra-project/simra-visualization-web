package visualization.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import visualization.service.IncidentService;
import visualization.service.RideService;
import visualization.web.resources.IncidentResource;
import visualization.web.resources.RideResource;

import java.util.List;


/*⁄ø

This is the place where we communicate with the frontend

 */

@RestController
public class RideController {

    @Autowired
    private RideService rideService;

    @GetMapping(value = "/rides/{rideId}")
    public HttpEntity<RideResource> getRideByRideId(@PathVariable String rideId) {
        return ResponseEntity.ok(rideService.getRideById(rideId));
    }

    @GetMapping(value = "/rides")
    public HttpEntity<List<RideResource>> getRidesNear(@RequestParam(value = "lon") double longitude,
                                                               @RequestParam(value = "lat") double latitude,
                                                               @RequestParam(value = "max") int maxDistance) {
        return ResponseEntity.ok(rideService.getRidesInRange(longitude, latitude, maxDistance));
    }

}
