package visualization.web.resources;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.ArrayList;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude
public class StatisticsResource {

    @JsonProperty
    private ArrayList ridesStatistics = new ArrayList<RideStatisticsResource>();

    @JsonProperty
    private ArrayList incidentsStatistics = new ArrayList<IncidentStatisticsResource>();

}
