package visualization.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import visualization.service.StatisticsService;
import visualization.web.resources.StatisticsResource;

import static com.mongodb.client.model.Filters.eq;


@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
public class StatisticsController {

    @Autowired
    private StatisticsService statisticsService;

    @GetMapping(value = "/statistics")
    public HttpEntity<StatisticsResource> getStatisticsByRegion(@RequestParam(value = "region") String region) {
        return ResponseEntity.ok(statisticsService.getStatisticsByRegion(region));
    }
}
