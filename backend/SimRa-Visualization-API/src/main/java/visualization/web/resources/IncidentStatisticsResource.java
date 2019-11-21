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
public class IncidentStatisticsResource {

    @JsonProperty
    int bikeType;

    @JsonProperty
    Long ts;

    @JsonProperty
    boolean childInvolved;

    @JsonProperty
    boolean trailerInvolved;

    @JsonProperty
    int incidentType;

    @JsonProperty
    boolean scary;

    @JsonProperty
    Boolean i1Bus;

    @JsonProperty
    Boolean i2Cyclist;

    @JsonProperty
    Boolean i3Pedestrian;

    @JsonProperty
    Boolean i4DeliveryVan;

    @JsonProperty
    Boolean i5Truck;

    @JsonProperty
    Boolean i6Motorcycle;

    @JsonProperty
    Boolean i7Car;

    @JsonProperty
    Boolean i8Taxi;

    @JsonProperty
    Boolean i9Other;

    @JsonProperty
    Boolean i10EScooter;

    // add more Stats Metadata for Incidents here
}
