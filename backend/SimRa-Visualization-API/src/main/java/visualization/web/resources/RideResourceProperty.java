package visualization.web.resources;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.ArrayList;

/*
Representation of a Ride
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude
public class RideResourceProperty {

    @JsonProperty
    private String rideId;

    @JsonProperty
    private ArrayList ts;

    @JsonProperty
    private Float distance;

    @JsonProperty
    private Long duration;

    @JsonProperty
    private String weekday;
}
