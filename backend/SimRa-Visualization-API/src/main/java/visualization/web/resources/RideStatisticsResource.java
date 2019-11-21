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
    Long startTime;

    @JsonProperty
    int duration;

    // add more Stats Metadata for Rides here
    //Co2
    //driven KMs
    // ...

}
