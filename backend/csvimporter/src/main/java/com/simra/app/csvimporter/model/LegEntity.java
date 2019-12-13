package com.simra.app.csvimporter.model;

import lombok.*;
import net.minidev.json.annotate.JsonIgnore;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.geo.GeoJsonLineString;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Objects;

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

    @JsonIgnore
    private Boolean markForRemoval = false;

    //Lombock and Kotlin seem to have difficulties interacting. Looking for a nicer fix
    public void setGeometryForKotlin(GeoJsonLineString geometry) {
        this.geometry = geometry;
    }

    public GeoJsonLineString getGeometryForKotlin() {
        return this.geometry;
    }

    public void setPropertiesForKotlin(LegPropertyEntity properties) {
        this.properties = properties;
    }

    public LegPropertyEntity getPropertiesForKotlin() {
        return properties;
    }

    public void setMarkForRemovalForKotlin(Boolean remove) {
        this.markForRemoval = remove;
    }

    public Boolean getMarkForRemovalForKotlin() {
        return this.markForRemoval;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LegEntity legEntity = (LegEntity) o;
        return Objects.equals(geometry.getCoordinates(), legEntity.geometry.getCoordinates()) &&
                Objects.equals(properties, legEntity.properties);
    }


    @Override
    public int hashCode() {
        return Objects.hash(geometry, properties);
    }
}
