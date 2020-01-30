package visualization.web.resources;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.core.mapping.Document;
import visualization.web.resources.serializers.GeoJsonPointSerializer;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "regions")
public class RegionResource {

    @Id
    private String id;

    @JsonSerialize(using = GeoJsonPointSerializer.class)
    private GeoJsonPoint geometry;

    private RegionResourceProperty properties;
}
