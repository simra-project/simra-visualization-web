package visualization.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import visualization.service.StatisticsService;
import visualization.web.resources.StatisticsResource;

import java.io.IOException;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
public class StatisticsController {

    @Autowired
    private StatisticsService statisticsService;

    @Cacheable(value = "statistics")
    @GetMapping(value = "/statistics")
    public HttpEntity<StatisticsResource> getStatisticsByRegion(@RequestParam(value = "region") String region) throws IOException {
        System.out.println("No cache hit - Executing /statistics");

        return ResponseEntity.ok(statisticsService.getStatisticsByRegion(region));
    }
}
