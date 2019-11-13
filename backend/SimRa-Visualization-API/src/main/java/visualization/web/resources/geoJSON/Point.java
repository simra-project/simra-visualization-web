package visualization.web.resources.geoJSON;

import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonTypeName("Point")
public class Point extends Geometry {

	private Coordinates[] coordinates;

	private Point() {
		super(Point.class.getSimpleName());
	}

	public Point(double longitude, double latitude) {
		this();
		coordinates[0].setLongitude(longitude);
		coordinates[1].setLatitude(latitude);
	}

	public Point(double longitude, double latitude, double altitude, long timestamp) {
		this();
		coordinates[0].setLongitude(longitude);
		coordinates[1].setLatitude(latitude);
		coordinates[2].setAltitude(altitude);
		coordinates[3].setTimestamp(timestamp);
	}


	public Point getCoordinates() {
		return coordinates;
	}

	public void setCoordinates(Coordinates[] coordinates) {
		this.coordinates = coordinates;
	}

}
