package visualization.data.mongodb.entities;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.geo.GeoJsonLineString;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "rides")
public class RideEntity {

    @Id
    private String id;

    private GeoJsonLineString location;

    private GeoJsonLineString locationMapMatched;

    private ArrayList<Long> ts;

    private ArrayList<Long> tsMapMatched;

    private Float distance;

    public Long getDuration() {
        return ts.get(ts.size() - 1) - ts.get(0);
    }
}
