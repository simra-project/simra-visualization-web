package visualization.data.mongodb.entities;

import lombok.*;
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

    private int incidentCountWithTrailersInvolved;

    private int incidentCountScary;

    private int profileCount;

    private int profileCountMale;

    private int profileCountFemale;

    private int profileCountOther;

    private ArrayList<String> profileBikeTypeLabels;

    private ArrayList<Integer> profileBikeTypeData;

    private ArrayList<String> profileAgeDistributionLabels;

    private ArrayList<String> profileAgeDistributionData;

    private ArrayList<String> profileAgeDistributionDataMale;

    private ArrayList<String> profileAgeDistributionDataFemale;

    private ArrayList<String> profileAgeDistributionDataOther;

    private ArrayList<String> profileAgeGroupCrossData;

    private ArrayList<String> profileAgeGroupCrossTotal;
}
