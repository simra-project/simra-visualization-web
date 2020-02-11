package visualization.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import visualization.service.IncidentService;
import visualization.web.resources.IncidentResource;

import java.util.List;

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
        return ResponseEntity.ok(incidentService.getIncidentsByFileId(rideId));
    }

    /**
     * Example request: http://localhost:8080/incidents?lon=13.26155&lat=52.4308&max=5000
     *
     * @param longitude   Coordinate-Longitude like 13.26155
     * @param latitude    Coordinate-Latitude like 52.4308
     * @param maxDistance maximum (distance) radius around the given coordinate point like 5000
     * @return list of incidents in the circle around the coordinate using maxDistance as the radius
     */
    @Cacheable(value = "radiusIncidents")
    @GetMapping(value = "/incidents")
    public HttpEntity<List<IncidentResource>> getIncidentsNear(@RequestParam(value = "lon") double longitude,
                                                               @RequestParam(value = "lat") double latitude,
                                                               @RequestParam(value = "max") int maxDistance) {
        System.out.println("No cache hit - Executing /incidents");

        return ResponseEntity.ok(incidentService.getIncidentsInRange(longitude, latitude, maxDistance));
    }

    /**
     * Example request: http://localhost:8080/incidents/area?bottomleft=13.297089,52.481744&topright=13.456360,52.547463
     *
     * @param bottomleft Coordinate-Tuple like 13.297089,52.481744
     * @param topright   Coordinate-Tuple like 13.456360,52.547463
     * @return list of incidents in the given area
     */
    @Cacheable(value = "areaIncidents")
    @GetMapping(value = "/incidents/area")
    public HttpEntity<List<IncidentResource>> getIncidentsWithin(@RequestParam(value = "bottomleft") double[] bottomleft,
                                                                 @RequestParam(value = "topright") double[] topright) {
        System.out.println("No cache hit - Executing /incidents/area");

        return ResponseEntity.ok(incidentService.getIncidentsInWithin(new GeoJsonPoint(bottomleft[0], bottomleft[1]),
                new GeoJsonPoint(bottomleft[0], topright[1]),
                new GeoJsonPoint(topright[0], topright[1]),
                new GeoJsonPoint(topright[0], bottomleft[1])));
    }

    // get all incidents with filter criteria applied
    @Cacheable(value = "filteredIncidents")
    @GetMapping(value = "/incidents/filter")
    public HttpEntity<List<IncidentResource>> getIncidentsFilteredBy(@RequestParam(value = "bottomleft") double[] first,
                                                                     @RequestParam(value = "topright") double[] second,
                                                                     @RequestParam(value = "fromTs", required = false) Long fromTs,
                                                                     @RequestParam(value = "untilTs", required = false) Long untilTs,
                                                                     @RequestParam(value = "fromMinutesOfDay", required = false) Integer fromMinutesOfDay,
                                                                     @RequestParam(value = "untilMinutesOfDay", required = false) Integer untilMinutesOfDay,
                                                                     @RequestParam(value = "weekdays", required = false) String[] weekdays,
                                                                     @RequestParam(value = "bike", required = false) Integer[] bikeTypes,
                                                                     @RequestParam(value = "child", required = false) Boolean child,
                                                                     @RequestParam(value = "trailer", required = false) Boolean trailer,
                                                                     @RequestParam(value = "incidents", required = false) Integer[] incidentTypes,
                                                                     @RequestParam(value = "participants", required = false) Boolean[] participants,
                                                                     @RequestParam(value = "scary", required = false) Boolean scary,
                                                                     @RequestParam(value = "description", required = false) Boolean description) {
        System.out.println("No cache hit - Executing /incidents/filter");

        return ResponseEntity.ok(incidentService.getFilteredIncidents(new GeoJsonPoint(first[0], first[1]),
                new GeoJsonPoint(first[0], second[1]),
                new GeoJsonPoint(second[0], second[1]),
                new GeoJsonPoint(second[0], first[1]), fromTs, untilTs, fromMinutesOfDay, untilMinutesOfDay, weekdays, bikeTypes, child, trailer, incidentTypes, participants, scary, description));
    }

}
