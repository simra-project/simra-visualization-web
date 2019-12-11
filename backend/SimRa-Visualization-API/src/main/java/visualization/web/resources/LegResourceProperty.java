package visualization.web.resources;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

/*
Representation of a Leg
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude
public class LegResourceProperty {

    @JsonProperty
    private Integer weight;

    //Lombock and Kotlin seem to have difficulties interacting. Looking for a nicer fix
    public void setWeightForKotlin(Integer weight) {
        this.weight = weight;
    }
}
