package visualization.web.resources;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude
public class StatisticsResource {

    //tbd
    private String id;

    private String region;
}
