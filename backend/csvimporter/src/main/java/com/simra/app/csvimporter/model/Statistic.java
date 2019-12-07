package com.simra.app.csvimporter.model;

import org.bson.Document;

import java.util.Date;
import java.util.List;

public class Statistic {
    public String region;

    public int rideCount;
    public Long accumulatedDuration;
    public Double accumulatedDistance;
    public Double accumulatedSavedCO2;
    public Long averageDuration;
    public Double averageDistance;
    public Double averageSpeed;

    public int incidentCount;
    public List<String> incidentTypeLabels;
    public List<Integer> incidentTypeData;
    public List<String> incidentParticipantTypeLabels;
    public List<Integer> incidentParticipantTypeData;
    public Integer incidentCountWithChildrenInvolved;
    public Integer incidentCountWithTrailersInvolved;
    public Integer incidentCountScary;

    public int profileCount;
    public int profileCountMale;
    public int profileCountFemale;
    public int profileCountOther;
    public List<String> profileBikeTypeLabels;
    public List<Integer> profileBikeTypeData;
    public List<String> profileAgeDistributionLabels;
    public List<Integer> profileAgeDistributionData;
    public List<Integer> profileAgeDistributionDataMale;
    public List<Integer> profileAgeDistributionDataFemale;
    public List<Integer> profileAgeDistributionDataOther;
    public Statistic() {
    }

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
        statistic.put("incidentTypeLabels", incidentTypeLabels);
        statistic.put("incidentTypeData", incidentTypeData);
        statistic.put("incidentParticipantTypeLabels", incidentParticipantTypeLabels);
        statistic.put("incidentParticipantTypeData", incidentParticipantTypeData);
        statistic.put("incidentCountWithChildrenInvolved", incidentCountWithChildrenInvolved);
        statistic.put("incidentCountCountWithTrailersInvolved", incidentCountWithTrailersInvolved);
        statistic.put("incidentCountScary", incidentCountScary);

        statistic.put("profileCount", profileCount);
        statistic.put("profileCountMale", profileCountMale);
        statistic.put("profileCountFemale", profileCountFemale);
        statistic.put("profileCountOther", profileCountOther);
        statistic.put("profileBikeTypeLabels", profileBikeTypeLabels);
        statistic.put("profileBikeTypeData", profileBikeTypeData);
        statistic.put("profileAgeDistributionLabels", profileAgeDistributionLabels);
        statistic.put("profileAgeDistributionData", profileAgeDistributionData);
        statistic.put("profileAgeDistributionDataMale", profileAgeDistributionDataMale);
        statistic.put("profileAgeDistributionDataFemale", profileAgeDistributionDataFemale);
        statistic.put("profileAgeDistributionDataOther", profileAgeDistributionDataOther);
        return statistic;
    }
}
