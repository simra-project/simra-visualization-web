package visualization.web.resources;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import net.minidev.json.JSONObject;
import org.springframework.boot.jackson.JsonObjectDeserializer;

import java.util.ArrayList;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@JsonInclude
public class StatisticsAgeGroupResource {

    @JsonProperty
    private Long accumulatedDistance;

    //TODO: Sollte eigentlich als Long aus der DB kommen
    @JsonProperty
    private JSONObject accumulatedDuration;

    @JsonProperty
    private Long accumulatedSavedCO2;

    @JsonProperty
    private String ageGroup;

    @JsonProperty
    private Long averageDistance;

    //TODO: Sollte eigentlich als Long aus der DB kommen
    @JsonProperty
    private JSONObject averageDuration;

    @JsonProperty
    private Long averageSavedCO2;

    @JsonProperty
    private Long averageScaryIncidentCount;

    @JsonProperty
    private Long averageSpeed;

    @JsonProperty
    private Integer bikerCount;

    @JsonProperty
    private Integer rideCount;

    @JsonProperty
    private Integer scaryIncidentCount;
}
