package visualization.web.resources;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import visualization.web.resources.geoJSON.MultiPoint;

import java.util.List;

/*
Representation of multiple Incidents
 */

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude
public class MultipleIncidentResource {

    @JsonProperty
    private List<IncidentResource> incidents;
}
