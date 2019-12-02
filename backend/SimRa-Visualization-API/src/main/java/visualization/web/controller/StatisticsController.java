package visualization.web.controller;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static com.mongodb.client.model.Filters.eq;


@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
public class StatisticsController {

    @GetMapping(value = "/statistics")
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
    }
}
