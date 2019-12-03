package visualization.web.resources;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

/*
Representation of an Incident
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude
public class IncidentResourceProperty {

    @JsonProperty
    private String rideId;

    @JsonProperty
    private int key;

    @JsonProperty
    private long ts;

    @JsonProperty
    private Boolean child;

    @JsonProperty
    private Boolean trailer;

    @JsonProperty
    private int phoneLocation;

    @JsonProperty
    private int incidentType;

    @JsonProperty
    private Boolean scary;

    @JsonProperty
    private String description;

    @JsonProperty
    private Boolean i1Bus;

    @JsonProperty
    private Boolean i2Cyclist;

    @JsonProperty
    private Boolean i3Pedestrian;

    @JsonProperty
    private Boolean i4DeliveryVan;

    @JsonProperty
    private Boolean i5Truck;

    @JsonProperty
    private Boolean i6Motorcycle;

    @JsonProperty
    private Boolean i7Car;

    @JsonProperty
    private Boolean i8Taxi;

    @JsonProperty
    private Boolean i9Other;

    @JsonProperty
    private Boolean i10EScooter;

}
