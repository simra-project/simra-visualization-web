package visualization.web.resources;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class RegionResourceProperty {

    private String name;

    private Integer defaultZoomLevel;

    private Integer maxZoomLevel;

    private Double[] bottomLeftBound;

    private Double[] topRightBound;

    private String description;
}
