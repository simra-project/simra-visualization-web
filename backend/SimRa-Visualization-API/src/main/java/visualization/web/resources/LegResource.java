package visualization.web.resources;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.*;
import org.springframework.data.mongodb.core.geo.GeoJsonLineString;
import visualization.web.resources.serializers.GeoJsonLineStringSerializer;

/*
Representation of a Leg
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude
public class LegResource {

    @JsonProperty
    private String type = "Feature";

    @JsonSerialize(using = GeoJsonLineStringSerializer.class)
    private GeoJsonLineString geometry;

    @JsonProperty
    private LegResourceProperty properties;

    //Lombock and Kotlin seem to have difficulties interacting. Looking for a nicer fix
    public void setGeometryForKotlin(GeoJsonLineString geometry) {
        this.geometry = geometry;
    }

    public void setPropertiesKotlin(LegResourceProperty properties) {
        this.properties = properties;
    }
}
