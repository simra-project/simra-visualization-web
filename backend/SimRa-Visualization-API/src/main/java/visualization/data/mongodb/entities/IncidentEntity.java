package visualization.data.mongodb.entities;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.geo.Point;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Document(collection="incidents")
public class IncidentEntity {

    //TODO: Build Entity according to MongoDB Schema or vice versa
    //https://stackoverflow.com/questions/56624690/composite-primary-key-using-mongodb-and-spring-data-jpa
    @Id
    private CompositeKey id;

    private int rideId;

    private int key;

    private Point coordinates;

    private long ts;

    private Boolean child;

    private Boolean trailer;

    private int phoneLocation;

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
        private int rideId;
        private int key;
    }

}
