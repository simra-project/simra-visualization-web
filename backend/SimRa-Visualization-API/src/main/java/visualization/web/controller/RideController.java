package visualization.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.geo.Point;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import visualization.service.RideService;
import visualization.web.resources.RideResource;

import java.util.LinkedList;
import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/rides")
public class RideController {

    @Autowired
    private RideService rideService;

    // gets raw ride by rideId
    @GetMapping(value = "/raw/{rideId}")
    public HttpEntity<RideResource> getRawRideByRideId(@PathVariable String rideId) {
        return ResponseEntity.ok(rideService.getRawRideById(rideId));
    }

    // gets mapMatched ride by rideId
    @GetMapping(value = "/mapmatched/{rideId}")
    public HttpEntity<RideResource> getMapMatchedByRideId(@PathVariable String rideId) {
        return ResponseEntity.ok(rideService.getMapMatchedRideById(rideId));
    }


    // get all rides in range minDistance and maxDistance around a Point (longitude, latitude)
    @Cacheable(value = "radiusRides")
    @GetMapping
    public HttpEntity<List<RideResource>> getRidesNear(@RequestParam(value = "lon") double longitude,
                                                       @RequestParam(value = "lat") double latitude,
                                                       @RequestParam(value = "max") int maxDistance) {
        System.out.println("No cache hit - Executing /rides");

        return ResponseEntity.ok(rideService.getRidesInRange(longitude, latitude, maxDistance));
    }

    // example: http://localhost:8080/rides/area?bottomleft=13.297089,52.481744&topright=13.456360,52.547463
    @Cacheable(value = "areaRides")
    @GetMapping(value = "/area")
    public HttpEntity<List<RideResource>> getRidesWithin(@RequestParam(value = "bottomleft") double[] first,
                                                         @RequestParam(value = "topright") double[] second) {
        System.out.println("No cache hit - Executing /rides/area");

        return ResponseEntity.ok(rideService.getRidesWithin(new GeoJsonPoint(first[0], first[1]),
                new GeoJsonPoint(first[0], second[1]),
                new GeoJsonPoint(second[0], second[1]),
                new GeoJsonPoint(second[0], first[1])));
    }

    // example: http://localhost:8080/rides/polygon?coords=13.297089,52.481744,13.456360,52.481744,13.456360,52.547463,13.297089,52.547463
    @Cacheable(value = "polygonRides")
    @GetMapping(value = "/polygon")
    public HttpEntity<List<RideResource>> getRidesWithinPolygon(@RequestParam(value = "coords") double[] coords) {
        System.out.println("No cache hit - Executing /rides/polygon");

        List<Point> points = new LinkedList<>();
        for (int i=0; i<coords.length/2; i++) {
            points.add(new GeoJsonPoint(coords[i*2], coords[i*2+1]));
        }
        points.add(points.get(0));

        return ResponseEntity.ok(rideService.getRidesWithinPolygon(points));
    }

    @GetMapping(value = "/from/{fromTs}/to/{untilTs}")
    public HttpEntity<List<RideResource>> getRidesAtTime(@PathVariable Long fromTs,
                                                         @PathVariable Long untilTs) {
        return ResponseEntity.ok(rideService.getRidesAtTime(fromTs, untilTs));
    }

    //checks if a specific ride is already imported
    @GetMapping(value = "/status/{rideId}")
    public ResponseEntity<Boolean> isRideImported(@PathVariable String rideId) {
        return ResponseEntity.ok(rideService.isRideImported(rideId));
    }

    //counts all imported rides
    @GetMapping(value = "/status/count")
    public ResponseEntity<Long> getImportedRidesCount() {
        return ResponseEntity.ok(rideService.getImportedRidesCount());
    }
}
