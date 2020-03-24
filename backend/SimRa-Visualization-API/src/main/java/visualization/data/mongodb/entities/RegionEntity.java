package visualization.data.mongodb.entities;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.core.mapping.Document;
import visualization.service.serializers.GeoJsonPointDeserializer;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "regions")
public class RegionEntity {

    @Id
    private String id;

    @JsonDeserialize(using = GeoJsonPointDeserializer.class)
    private GeoJsonPoint location;

    private String name;

    private Integer defaultZoomLevel;

    private Integer maxZoomLevel;

    private Double[] bottomLeftBound;

    private Double[] topRightBound;

    private String description;
}
