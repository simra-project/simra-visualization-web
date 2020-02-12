package visualization.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CachePut;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import visualization.service.LegService;
import visualization.web.resources.LegResource;

import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
public class LegController {

    @Autowired
    private LegService legService;

    // example: http://localhost:8080/legs/area?bottomleft=13.297089,52.481744&topright=13.456360,52.547463&minweight=1
    @CachePut(value="legsWithin")
    @GetMapping(value = "/legs/area")
    public HttpEntity<List<LegResource>> getLegsWithin(@RequestParam(value = "bottomleft") double[] first,
                                                       @RequestParam(value = "topright") double[] second,
                                                       @RequestParam(value = "minWeight") int minWeight,
                                                       @RequestParam(value = "weekdays", required = false) String day,
                                                       @RequestParam(value = "minDistance", required = false) Double minDist,
                                                       @RequestParam(value = "maxDistance", required = false) Double maxDist,
                                                       @RequestParam(value = "fromMinutesOfDay", required = false) Integer startTime,
                                                       @RequestParam(value = "untilMinutesOfDay", required = false) Integer endTime)
    {
        return ResponseEntity.ok(legService.getLegsWithin(new GeoJsonPoint(first[0], first[1]),
                new GeoJsonPoint(first[0], second[1]),
                new GeoJsonPoint(second[0], second[1]),
                new GeoJsonPoint(second[0], first[1]),
                minWeight,
                day,
                minDist,
                maxDist,
                startTime,
                endTime));
    }
}
