package visualization.web.resources;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.ArrayList;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@JsonInclude
public class StatisticsAgeGroupResource {

    @JsonProperty
    private Double accumulatedDistance;

    @JsonProperty
    private Long accumulatedDuration;

    @JsonProperty
    private Double accumulatedSavedCO2;

    @JsonProperty
    private String ageGroup;

    @JsonProperty
    private Double averageDistance;

    @JsonProperty
    private Double averageDuration;

    @JsonProperty
    private Double averageSavedCO2;

    @JsonProperty
    private Double averageScaryIncidentCount;

    @JsonProperty
    private Double averageSpeed;

    @JsonProperty
    private Integer bikerCount;

    @JsonProperty
    private Integer rideCount;

    @JsonProperty
    private Integer scaryIncidentCount;
}
