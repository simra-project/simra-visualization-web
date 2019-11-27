package visualization.web.resources;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.ArrayList;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude
public class IncidentStatisticsResource {

    @JsonProperty
    int incidentCount;

    @JsonProperty
    ArrayList<String> bikeTypeLabels;

    @JsonProperty
    ArrayList<Integer> bikeTypeData;

    @JsonProperty
    ArrayList<String> incidentTypeLabels;

    @JsonProperty
    ArrayList<Integer> incidentTypeData;

    @JsonProperty
    ArrayList<String> participantTypeLabels;

    @JsonProperty
    ArrayList<Integer> participantTypeData;

    @JsonProperty
    Integer countChildrenInvolved;

    @JsonProperty
    Integer countTrailersInvolved;

    @JsonProperty
    Integer countOfScary;

    // add more Stats Metadata for Incidents here
}
