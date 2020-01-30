package visualization.data.mongodb.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.json.JSONObject;
import org.json.JSONString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "statistics")
public class StatisticsEntity {

    @Id
    private String id;

    private String region;

    private long timestamp;

    private int rideCount;

    private Double accumulatedDuration;

    private Double accumulatedDistance;

    private Double accumulatedSavedCO2;

    private Long averageDuration;

    private Double averageDistance;

    private Double averageSpeed;

    private int incidentCount;

    private ArrayList<String> incidentTypeLabels;

    private ArrayList<Integer> incidentTypeData;

    private ArrayList<String> incidentParticipantTypeLabels;

    private ArrayList<Integer> incidentParticipantTypeData;

    private int incidentCountWithChildrenInvolved;

    //2x Count in DB so lassen?
    private int incidentCountCountWithTrailersInvolved;

    private int incidentCountScary;

    private int profileCount;

    private int profileCountMale;

    private int profileCountFemale;

    private int profileCountOther;

    private ArrayList<String> profileBikeTypeLabels;

    private ArrayList<Integer> profileBikeTypeData;

    private ArrayList<String> profileAgeDistributionLabels;

    private ArrayList<Integer> profileAgeDistributionData;

    private ArrayList<Integer> profileAgeDistributionDataMale;

    private ArrayList<Integer> profileAgeDistributionDataFemale;

    private ArrayList<Integer> profileAgeDistributionDataOther;

    private ArrayList<String> profileAgeGroupCrossData;

    private String profileAgeGroupCrossTotal;
}
