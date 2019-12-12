package com.simra.app.csvimporter.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.simra.app.csvimporter.model.serializer.GeoJsonLineStringSerializer;
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
    private String legId;

    private String type = "Feature";

    @JsonSerialize(using = GeoJsonLineStringSerializer.class)
    private GeoJsonLineString geometry;

    private LegPropertyEntity properties;
}
