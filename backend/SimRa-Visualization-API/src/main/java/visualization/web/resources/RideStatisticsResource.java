package visualization.web.resources;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import javax.management.DescriptorKey;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude
public class RideStatisticsResource {

    @JsonProperty
    Long startTime;

    @JsonProperty
    int duration;

    @JsonProperty
    Float length;

    @JsonProperty
    Float savedCO2;
    // add more Stats Metadata for Rides here
}
