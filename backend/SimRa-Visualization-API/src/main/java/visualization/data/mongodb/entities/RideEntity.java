package visualization.data.mongodb.entities;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.geo.GeoJsonMultiPoint;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Document(collection="rides")
public class RideEntity {

    //TODO: Build Entity according to MongoDB Schema (CSV Importer Schema) or vice versa
    //https://stackoverflow.com/questions/56624690/composite-primary-key-using-mongodb-and-spring-data-jpa
    //TODO: rideId als String
    @Id
    private int rideId;

    //TODO: Find possibility to add timestamp to each coordinate point (should be valid for the whole project)
    private GeoJsonMultiPoint location;

    /* OR:

    private List<GeoJsonPoint> location;

     */

}
