package com.simra.app.csvimporter.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

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

    private Set<String> fileIdSet = new HashSet<>();

    private Integer fileIdSetCount;

    public void setFileIdSetForKotlin(Set<String> fileIdSet) {
        this.fileIdSet = fileIdSet;
        this.fileIdSetCount = fileIdSet.size();
    }

    public Set<String> getFileIdSetForKotlin() {
        return fileIdSet;
    }
}
