package com.simra.app.csvimporter.service;

import com.mongodb.client.model.geojson.Point;
import com.mongodb.client.model.geojson.Position;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBeanBuilder;
import com.simra.app.csvimporter.controller.IncidentRepository;
import com.simra.app.csvimporter.model.IncidentEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.*;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class IncidentParserThreaded implements Runnable {
    private static final Logger LOG = LoggerFactory.getLogger(IncidentParserThreaded.class);


    private String fileName;

    private IncidentRepository incidentRepository;
    private String csvString;
    private String region;

    public IncidentParserThreaded(String fileName, IncidentRepository incidentRepository, String csvString, String region) {

        this.incidentRepository = incidentRepository;
        this.csvString = csvString;
        this.fileName = fileName;
        this.region = region;
    }

    @Override
    public void run() {
        LOG.info("Incident parser running {}", this.fileName);
        try (BufferedReader reader = new BufferedReader(new StringReader(this.csvString))) {
            String line = reader.readLine();
            String[] arrOfStr = line.split("#");

            StringBuilder incidentCSVwithHeader = new StringBuilder();
            while (line != null) {
                line = reader.readLine();

                if (line != null) {
                    if (line.contains("==")) {
                        break;
                    }
                    incidentCSVwithHeader.append(line);
                    incidentCSVwithHeader.append("\r\n");
                }
            }

            ColumnPositionMappingStrategy<IncidentEntity> strategy = new ColumnPositionMappingStrategy<>();
            strategy.setType(IncidentEntity.class);
            String[] memberFieldsToBindTo = {"key", "lat", "lon", "ts", "bike", "childCheckBox", "trailerCheckBox", "pLoc", "incident", "i1", "i2", "i3", "i4", "i5", "i6", "i7", "i8", "i9", "scary", "desc", "i10"};
            strategy.setColumnMapping(memberFieldsToBindTo);

            List<IncidentEntity> incidentBeans = new CsvToBeanBuilder<IncidentEntity>(new StringReader(incidentCSVwithHeader.toString()))
                    .withMappingStrategy(strategy)
                    .withSkipLines(1)
                    .withIgnoreLeadingWhiteSpace(true)
                    .build().parse();
            incidentBeans = incidentBeans.stream().filter(item -> item.getTs() != 0).collect(Collectors.toList());

            incidentBeans.forEach(item -> {
                item.setFileId(this.fileName);
                item.setAppVersion(arrOfStr[0]);
                item.setFileVersion(Integer.parseInt(arrOfStr[1]));
                item.setRegion(region);
                List<Double> places = Arrays.asList(Double.parseDouble(item.getLon()), Double.parseDouble(item.getLat()));
                Point geoPoint = new Point(new Position(places));
                item.setLocation(geoPoint);
                item.setAddedAt(new Date());
                item.cleanDesc();

                item.setMinuteOfDay(Utils.getMinuteOfDay(item.getTs()));
                item.setWeekday(Utils.getWeekday(item.getTs()));

                HashMap<String, String> hm = new HashMap<>();
                hm.put("rideid", this.fileName);
                hm.put("key", String.valueOf(item.getKey()));
                item.setId(new HashMap(hm));


            });
            incidentRepository.saveAll(incidentBeans);

        } catch (Exception e) {
            LOG.error(String.valueOf(e));
        }
        LOG.info("Incident parser complete {}", this.fileName);
    }
}
