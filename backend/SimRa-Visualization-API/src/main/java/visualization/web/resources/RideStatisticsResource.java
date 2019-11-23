package visualization.web.resources;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude
public class RideStatisticsResource {

    @JsonProperty
    int rideCount;

    @JsonProperty
    int accumulatedDuration;

    @JsonProperty
    Float accumulatedDistance;

    @JsonProperty
    Float accumulatedSavedCO2;

    // add more Stats Metadata for Rides here
}
