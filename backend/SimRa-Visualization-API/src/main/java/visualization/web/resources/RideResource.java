package visualization.web.resources;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.*;
import org.springframework.data.mongodb.core.geo.GeoJsonLineString;
import visualization.web.resources.serializers.GeoJsonLineStringSerializer;

/*
Representation of a Ride
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude
public class RideResource {

    @JsonProperty
    private String type = "Feature";

    @JsonSerialize(using = GeoJsonLineStringSerializer.class)
    private GeoJsonLineString geometry;

    @JsonProperty
    private RideResourceProperty properties;

}
