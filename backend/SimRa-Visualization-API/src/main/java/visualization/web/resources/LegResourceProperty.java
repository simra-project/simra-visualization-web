package visualization.web.resources;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

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
public class LegResourceProperty {

    @JsonProperty
    private Set<String> fileIdSet;

    //Lombock and Kotlin seem to have difficulties interacting. Looking for a nicer fix
    public void setFileIdSetForKotlin(Set<String> fileIdSet) {
        this.fileIdSet = fileIdSet;
    }
}
