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

    /*@GetMapping(value = "/statistics")
    public HttpEntity<String> getStatistics(@RequestParam(value = "region") String region) {
        MongoDatabase db = (new MongoClient("localhost", 27017)).getDatabase("simra");
        MongoCollection<Document> collection = db.getCollection("statistics");

        Document statistic = collection.find(eq("region", region)).sort(new BasicDBObject("timestamp", -1)).limit(1).first();

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, "application/json; charset=UTF-8");

        if (statistic != null) {
            return ResponseEntity.ok().headers(headers).body(statistic.toJson());
        } else {
            return ResponseEntity.status(404).headers(headers).body("{ \"error\": \"No statistic found for this region.\" }");
        }
    }*/

    @GetMapping(value = "/statisticsnew")
    public HttpEntity<StatisticsResource> getStatisticsByRegion(@RequestParam(value = "region") String region) {
        return ResponseEntity.ok(statisticsService.getStatisticsByRegion(region));
    }
}
