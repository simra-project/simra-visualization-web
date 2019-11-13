package visualization.web.resources.geoJSON;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
public abstract class Geometry {

	protected String type;

	public String getType() {
		return type;
	}

}
