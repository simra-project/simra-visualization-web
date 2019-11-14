package visualization.web.resources.geoJSON;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
public abstract class Geometry {

	// all geometries are based on https://github.com/MPriess/GeoJSON-POJO

	protected String type;

	public String getType() {
		return type;
	}

}
