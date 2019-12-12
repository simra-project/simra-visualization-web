package com.simra.app.csvimporter.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.util.ArrayList;

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

    private ArrayList<String> fileIdList;

}
