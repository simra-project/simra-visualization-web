package visualization.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
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

    // example:  http://localhost:8080/rides/area?first=13.297089,52.481744&second=13.448689,52.509574&third=13.456360,52.547463&fourth=13.305468, 52.546459
    @GetMapping(value = "/rides/area")
    public HttpEntity<List<RideResource>> getRidesWithin(@RequestParam(value = "first") double[] first,
                                                         @RequestParam(value = "second")  double[] second,
                                                         @RequestParam(value = "third")  double[] third,
                                                         @RequestParam(value = "fourth")  double[] fourth) {
        return ResponseEntity.ok(rideService.getRidesWithin(new GeoJsonPoint(first[0], first[1]),
                                                            new GeoJsonPoint(second[0], second[1]),
                                                            new GeoJsonPoint(third[0], third[1]),
                                                            new GeoJsonPoint(fourth[0], fourth[1])));
    }

}
