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
    ArrayList<Integer> countEachBikeType;

    @JsonProperty
    Integer countChildrenInvolved;

    @JsonProperty
    Integer countTrailersInvolved;

    @JsonProperty
    ArrayList<Integer> countEachIncidentType;

    @JsonProperty
    Integer countOfScary;

    @JsonProperty
    Integer countI1Bus;

    @JsonProperty
    Integer countI2Cyclist;

    @JsonProperty
    Integer countI3Pedestrian;

    @JsonProperty
    Integer countI4DeliveryVan;

    @JsonProperty
    Integer countI5Truck;

    @JsonProperty
    Integer countI6Motorcycle;

    @JsonProperty
    Integer countI7Car;

    @JsonProperty
    Integer countI8Taxi;

    @JsonProperty
    Integer countI9Other;

    @JsonProperty
    Integer countI10EScooter;

    // add more Stats Metadata for Incidents here
}
