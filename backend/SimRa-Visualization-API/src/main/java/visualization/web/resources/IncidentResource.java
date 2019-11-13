package visualization.web.resources;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.ArrayList;
import java.util.HashMap;

/*
Representation of an Incident
 */

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude
public class IncidentResource {

    @JsonProperty
    private int rideId;

    @JsonProperty
    private int incidentId;

    @JsonProperty
    private ArrayList latlong;

    @JsonProperty
    private long timeStamp;
}
