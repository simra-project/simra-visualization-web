package visualization.web.resources;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.*;
import org.springframework.data.mongodb.core.geo.GeoJsonLineString;
import visualization.web.resources.serializers.GeoJsonLineStringSerializer;

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
}
