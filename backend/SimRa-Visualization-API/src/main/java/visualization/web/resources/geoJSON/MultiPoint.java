package visualization.web.resources.geoJSON;

import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonTypeName("MultiPoint")
public class MultiPoint extends Geometry {

	private List<double[]> coordinates;

}
