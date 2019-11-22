package visualization.web.resources;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude
public class StatisticsResource {

    @JsonProperty
    private RideStatisticsResource ridesStatistics = new RideStatisticsResource();

    @JsonProperty
    private IncidentStatisticsResource incidentsStatistics = new IncidentStatisticsResource();

}
