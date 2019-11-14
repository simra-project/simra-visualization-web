package visualization.web.resources.geoJSON;

import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonTypeName("Point")
public class Point extends Geometry {

	private double[] coordinates;

	private Point() {
		super(Point.class.getSimpleName());
	}

	public Point(double longitude, double latitude) {
		this();
		coordinates[0] = longitude;
		coordinates[1] = latitude;
	}

	public Point(double longitude, double latitude, double altitude) {
		this();
		coordinates[0] = longitude;
		coordinates[1] = latitude;
		coordinates[2] = altitude ;
	}


	public double[] getCoordinates() {
		return coordinates;
	}

	public void setCoordinates(double[] coordinates) {
		this.coordinates = coordinates;
	}

}
