package main.java.com.simra.app.csvimporter.model;

import org.bson.Document;

import java.util.Date;
import java.util.List;

public class Statistic implements MongoDocument {
    public String region;

    public int rideCount;
    public Long accumulatedDuration;
    public Double accumulatedDistance;
    public Double accumulatedSavedCO2;
    public Long averageDuration;
    public Double averageDistance;
    public Double averageSpeed;

    public int incidentCount;
    public List<String> incidentBikeTypeLabels;
    public List<Integer> incidentBikeTypeData;
    public List<String> incidentTypeLabels;
    public List<Integer> incidentTypeData;
    public List<String> incidentParticipantTypeLabels;
    public List<Integer> incidentParticipantTypeData;
    public Integer incidentCountWithChildrenInvolved;
    public Integer incidentCountWithTrailersInvolved;
    public Integer incidentCountScary;

    public Statistic() {
    }

    @Override
    public Document toDocumentObject() {
        Document statistic = new Document();
        statistic.put("region", region);
        statistic.put("timestamp", new Date());

        statistic.put("rideCount", rideCount);
        statistic.put("accumulatedDuration", accumulatedDuration);
        statistic.put("accumulatedDistance", accumulatedDistance);
        statistic.put("accumulatedSavedCO2", accumulatedSavedCO2);
        statistic.put("averageDuration", averageDuration);
        statistic.put("averageDistance", averageDistance);
        statistic.put("averageSpeed", averageSpeed);

        statistic.put("incidentCount", incidentCount);
        statistic.put("incidentBikeTypeLabels", incidentBikeTypeLabels);
        statistic.put("incidentBikeTypeData", incidentBikeTypeData);
        statistic.put("incidentTypeLabels", incidentTypeLabels);
        statistic.put("incidentTypeData", incidentTypeData);
        statistic.put("incidentParticipantTypeLabels", incidentParticipantTypeLabels);
        statistic.put("incidentParticipantTypeData", incidentParticipantTypeData);
        statistic.put("incidentCountWithChildrenInvolved", incidentCountWithChildrenInvolved);
        statistic.put("incidentCountCountWithTrailersInvolved", incidentCountWithTrailersInvolved);
        statistic.put("incidentCountScary", incidentCountScary);

        return statistic;
    }
}
