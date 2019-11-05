package visualization.web.resources;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

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
    private int incidentId;

    @JsonProperty
    private HashMap<String, Double> latlong;

    @JsonProperty
    private String description;
}
