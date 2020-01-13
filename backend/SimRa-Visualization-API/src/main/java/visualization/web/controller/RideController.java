package visualization.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import visualization.service.RideService;
import visualization.web.resources.RideResource;

import java.util.List;

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

    // example: http://localhost:8080/rides/area?bottomleft=13.297089,52.481744&topright=13.456360,52.547463
    @GetMapping(value = "/rides/area")
    public HttpEntity<List<RideResource>> getRidesWithin(@RequestParam(value = "bottomleft") double[] first,
                                                         @RequestParam(value = "topright") double[] second) {
        return ResponseEntity.ok(rideService.getRidesWithin(new GeoJsonPoint(first[0], first[1]),
                new GeoJsonPoint(first[0], second[1]),
                new GeoJsonPoint(second[0], second[1]),
                new GeoJsonPoint(second[0], first[1])));
    }

    //get rides between two timestamps
    @GetMapping(value = "/rides/time")
    public HttpEntity<List<RideResource>> getRidesBetweenTimestamps(@RequestParam(value = "startTs") Long startTs,
                                                                    @RequestParam(value= "endTs") Long endTs){
        return ResponseEntity.ok(rideService.getRidesAtTime(startTs, endTs));
    }

    //checks if a specific ride is already imported
    @GetMapping(value = "/rides/status/{rideId}")
    public ResponseEntity<Boolean> isRideImported(@PathVariable String rideId){
        return ResponseEntity.ok(rideService.isRideImported(rideId));
    }

    //counts all imported rides
    @GetMapping(value = "/rides/status/count")
    public ResponseEntity<Long> getImportedRidesCount(){
        return ResponseEntity.ok(rideService.getImportedRidesCount());
    }

}
