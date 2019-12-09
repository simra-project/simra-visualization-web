package visualization.web.resources;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.*;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import visualization.web.resources.serializers.GeoJsonPointSerializer;

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
    private String type = "Feature";

    @JsonSerialize(using = GeoJsonPointSerializer.class)
    private GeoJsonPoint geometry;

    @JsonProperty
    private IncidentResourceProperty properties;

}
