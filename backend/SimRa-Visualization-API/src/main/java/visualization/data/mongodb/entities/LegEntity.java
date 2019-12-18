package visualization.data.mongodb.entities;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.geo.GeoJsonLineString;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "leg")
public class LegEntity {
    @Id
    private String id;

    private String type;

    private GeoJsonLineString geometry;

    private LegPropertyEntity properties;
}
