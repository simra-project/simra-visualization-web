package visualization.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import visualization.service.RideService;
import visualization.web.resources.RideResource;

import java.util.List;


/*⁄

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

    // example: http://localhost:8080/rides/area?bottomleft=13.297089,52.481744&topright=13.456360,52.547463
    @GetMapping(value = "/rides/area")
    public HttpEntity<List<RideResource>> getRidesWithin(@RequestParam(value = "bottomleft") double[] first,
                                                         @RequestParam(value = "topright") double[] second) {
        return ResponseEntity.ok(rideService.getRidesWithin(new GeoJsonPoint(first[0], first[1]),
                new GeoJsonPoint(first[0], second[1]),
                new GeoJsonPoint(second[0], second[1]),
                new GeoJsonPoint(second[0], first[1])));
    }

    @GetMapping(value = "/rides/from/{fromTs}/to/{untilTs}")
    public HttpEntity<List<RideResource>> getRidesAtTime(@PathVariable Long fromTs,
                                                         @PathVariable Long untilTs) {
        return ResponseEntity.ok(rideService.getRidesAtTime(fromTs, untilTs));
    }

    // this might be useful later...
//    public HttpEntity<StatisticsResource> getFilteredStatistics(@RequestParam(value = "fromTs") Long fromTs,
//                                                                @RequestParam(value = "untilTs") Long untilTs,
//                                                                @RequestParam(value = "fromMinutesOfDay") Integer fromMinutesOfDay,
//                                                                @RequestParam(value = "untilMinutesOfDay") Integer untilMinutesOfDay,
//                                                                @RequestParam(value = "weekday") List<String> weekdays,
//                                                                @RequestParam(value = "bikeTypes") List<Integer> bikeTypes,
//                                                                @RequestParam(value = "incidentTypes") List<Integer> incidentTypes,
//                                                                @RequestParam(value = "childInvolved") Boolean childInvolved,
//                                                                @RequestParam(value = "trailerInvolved") Boolean trailerInvolved,
//                                                                @RequestParam(value = "scary") Boolean scary,
//                                                                @RequestParam(value = "participants") List<Integer> participants) {
//
//        return ResponseEntity.ok(statisticsService.getFilteredStatistics(fromTs, untilTs, fromMinutesOfDay, untilMinutesOfDay, weekdays, bikeTypes, incidentTypes, childInvolved, trailerInvolved, scary, parseParticipantsList(participants)));
//    }
//    private List<Boolean> parseParticipantsList(List<Integer> participants) {
//        List<Boolean> boolParticipants = new ArrayList<>();
//        if (participants.size() > 0) {
//            for (int i = 0; i < 10; i++) {
//                if (participants.contains(i)) {
//                    boolParticipants.add(true);
//                } else {
//                    boolParticipants.add(false);
//                }
//            }
//        }
//        return boolParticipants;
//    }
}
