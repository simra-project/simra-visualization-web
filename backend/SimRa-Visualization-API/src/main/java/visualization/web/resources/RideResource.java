package visualization.web.resources;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.springframework.data.mongodb.core.geo.GeoJsonMultiPoint;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;

import java.util.ArrayList;
import java.util.List;

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
    private int rideId;

    @JsonProperty
    private GeoJsonMultiPoint coordinates;

    @JsonProperty
    private ArrayList ts;

}
