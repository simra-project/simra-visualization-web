package visualization.web.resources.geoJSON;

import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@JsonTypeName("LineString")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LineString extends Geometry {

	private ArrayList<Double> coordinates;

}
