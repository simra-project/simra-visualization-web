package com.simra.app.csvimporter.model;

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

    private GeoJsonLineString geometry;

    private LegPropertyEntity properties;
}
