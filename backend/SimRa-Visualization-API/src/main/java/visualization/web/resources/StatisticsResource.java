package visualization.web.resources;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.springframework.data.annotation.Id;

import java.util.ArrayList;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude
public class StatisticsResource {

    @JsonProperty
    private String id;

    @JsonProperty
    private String region;

    @JsonProperty
    private long timestamp;

    @JsonProperty
    private int rideCount;

    @JsonProperty
    private Double accumulatedDuration;

    @JsonProperty
    private Double accumulatedDistance;

    @JsonProperty
    private Double accumulatedSavedCO2;

    @JsonProperty
    private Long averageDuration;

    @JsonProperty
    private Double averageDistance;

    @JsonProperty
    private Double averageSpeed;

    @JsonProperty
    private int incidentCount;

    @JsonProperty
    private ArrayList<String> incidentTypeLabels;

    @JsonProperty
    private ArrayList<Integer> incidentTypeData;

    @JsonProperty
    private ArrayList<String> incidentParticipantTypeLabels;

    @JsonProperty
    private ArrayList<Integer> incidentParticipantTypeData;

    @JsonProperty
    private int incidentCountWithChildrenInvolved;

    @JsonProperty
    private int incidentCountWithTrailersInvolved;

    @JsonProperty
    private int incidentCountScary;

    @JsonProperty
    private int profileCount;

    @JsonProperty
    private int profileCountMale;

    @JsonProperty
    private int profileCountFemale;

    @JsonProperty
    private int profileCountOther;

    @JsonProperty
    private ArrayList<String> profileBikeTypeLabels;

    @JsonProperty
    private ArrayList<Integer> profileBikeTypeData;

    @JsonProperty
    private ArrayList<String> profileAgeDistributionLabels;

    @JsonProperty
    private ArrayList<String> profileAgeDistributionData;

    @JsonProperty
    private ArrayList<String> profileAgeDistributionDataMale;

    @JsonProperty
    private ArrayList<String> profileAgeDistributionDataFemale;

    @JsonProperty
    private ArrayList<String> profileAgeDistributionDataOther;

    @JsonProperty
    private ArrayList<String> profileAgeGroupCrossData;

    @JsonProperty
    private ArrayList<String> profileAgeGroupCrossTotal;
}
