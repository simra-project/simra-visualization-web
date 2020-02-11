package com.simra.app.csvimporter;

import com.simra.app.csvimporter.service.MonitorService;
import com.simra.app.csvimporter.service.StatisticsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexType;
import org.springframework.data.mongodb.core.index.GeospatialIndex;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class CsvimporterApplication implements CommandLineRunner {
    private static final Logger logger = LoggerFactory.getLogger(CsvimporterApplication.class);

    @Autowired
    private MonitorService monitorService;

    @Autowired
    MongoTemplate mongoTemplate;


    public static void main(String[] args) {
        if (args.length > 0 && args[0].equals("--statistics")) {
            (new StatisticsService()).calculateStatistics();
            return;
        }

        SpringApplication.run(CsvimporterApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {

        GeospatialIndex geospatialIndexLocation=new GeospatialIndex("locationMapMatched");
        geospatialIndexLocation.typed(GeoSpatialIndexType.GEO_2DSPHERE);
        this.mongoTemplate.indexOps("rides").ensureIndex(geospatialIndexLocation);
        logger.info("Rides Location Indexed setup.");

        GeospatialIndex geospatialIndexLocationMapMatched=new GeospatialIndex("locationMapMatched");
        geospatialIndexLocationMapMatched.typed(GeoSpatialIndexType.GEO_2DSPHERE);
        this.mongoTemplate.indexOps("incidents").ensureIndex(geospatialIndexLocationMapMatched);
        logger.info("Rides LocationMapMatched Indexed setup.");

        GeospatialIndex geospatialIndexIncident=new GeospatialIndex("location");
        geospatialIndexIncident.typed(GeoSpatialIndexType.GEO_2DSPHERE);
        this.mongoTemplate.indexOps("incidents").ensureIndex(geospatialIndexIncident);
        logger.info("Incident Location Indexed setup.");



        monitorService.startRecursiveWatcher();
    }

}
