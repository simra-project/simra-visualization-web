package visualization.web.resources;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.springframework.data.mongodb.core.geo.GeoJsonLineString;

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
public class RideResource {

    @JsonProperty
    private String rideId;

    @JsonProperty
    private GeoJsonLineString coordinates;

    @JsonProperty
    private ArrayList ts;

}
