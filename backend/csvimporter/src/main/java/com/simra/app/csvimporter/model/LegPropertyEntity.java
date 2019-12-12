package com.simra.app.csvimporter.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.util.Objects;
import java.util.TreeSet;

/*
Representation of a Leg
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude
public class LegPropertyEntity {

    private TreeSet<String> fileIdSet = new TreeSet<>();

    public void setFileIdSetForKotlin(TreeSet<String> fileIdSet) {
        this.fileIdSet = fileIdSet;
    }

    public TreeSet<String> getFileIdSetForKotlin() {
        return fileIdSet;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LegPropertyEntity that = (LegPropertyEntity) o;
        return Objects.equals(fileIdSet, that.fileIdSet);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fileIdSet);
    }
}
