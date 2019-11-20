package visualization.data.mongodb.entities;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Document(collection="incidents")
public class IncidentEntity {

    @Id
    private CompositeKey id;

    private int rideId;

    private int key;

    private GeoJsonPoint location;

    private long ts;

    private Boolean childCheckBox;

    private Boolean trailerCheckBox;

    private int pLoc;

    private int incidentType;

    private Boolean scary;

    private String description;

    private Boolean i1Bus;

    private Boolean i2Cyclist;

    private Boolean i3Pedestrian;

    private Boolean i4DeliveryVan;

    private Boolean i5Truck;

    private Boolean i6Motorcycle;

    private Boolean i7Car;

    private Boolean i8Taxi;

    private Boolean i9Other;

    private Boolean i10EScooter;


    // Incident Key made of rideId and key
    @Getter
    @Setter
    @AllArgsConstructor
    public static class CompositeKey implements Serializable {
        private String rideId;
        private String key;
    }

}
