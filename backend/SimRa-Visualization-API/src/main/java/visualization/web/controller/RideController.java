package visualization.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import visualization.service.RideService;
import visualization.web.resources.RideResource;

import java.util.List;


/*⁄ø

This is the place where we communicate with the frontend

 */

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
public class RideController {

    @Autowired
    private RideService rideService;

    // gets ride by rideId
    @GetMapping(value = "/rides/{rideId}")
    public HttpEntity<RideResource> getRideByRideId(@PathVariable String rideId) {
        return ResponseEntity.ok(rideService.getRideById(rideId));
    }

    // get all rides in range minDistance and maxDistance around a Point (longitude, latitude)
    @GetMapping(value = "/rides")
    public HttpEntity<List<RideResource>> getRidesNear(@RequestParam(value = "lon") double longitude,
                                                       @RequestParam(value = "lat") double latitude,
                                                       @RequestParam(value = "max") int maxDistance) {
        return ResponseEntity.ok(rideService.getRidesInRange(longitude, latitude, maxDistance));
    }

    // get all incidents in Box geometry localhost/rides?bottomleft=lon,lat&upperright=lon,lat
    //TODO: Create Postman sample calls
    @GetMapping(value = "/rides")
    public HttpEntity<List<RideResource>> getRidesWithin(@RequestParam(value = "bottomleft") double[] bottomLeft,
                                                                 @RequestParam(value = "upperright")  double[] upperRight) {
        return ResponseEntity.ok(rideService.getRidesWithin(bottomLeft, upperRight));
    }

}
