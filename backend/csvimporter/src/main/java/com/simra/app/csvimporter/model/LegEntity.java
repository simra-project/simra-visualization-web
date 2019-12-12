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

    //Lombock and Kotlin seem to have difficulties interacting. Looking for a nicer fix
    public void setGeometryForKotlin(GeoJsonLineString geometry) {
        this.geometry = geometry;
    }

    public GeoJsonLineString getGeometryForKotlin() {
        return this.geometry;
    }
}
