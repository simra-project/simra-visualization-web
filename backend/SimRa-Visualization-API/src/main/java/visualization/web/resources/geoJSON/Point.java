package visualization.web.resources.geoJSON;

import com.fasterxml.jackson.annotation.JsonTypeName;

import java.util.ArrayList;

@JsonTypeName("Point")
public class Point extends Geometry {

	private ArrayList<Double> coordinates;

	private Point() {
		super(Point.class.getSimpleName());
	}

	public Point(double longitude, double latitude) {
		this();
		coordinates.add(0, longitude);
		coordinates.add(1, longitude);
	}

	public Point(double longitude, double latitude, double altitude) {
		this();
		coordinates.add(0, longitude);
		coordinates.add(1, longitude);
		coordinates.add(2, altitude);
	}


	public ArrayList<Double> getCoordinates() {
		return coordinates;
	}

	public void setCoordinates(ArrayList<Double> coordinates) {
		this.coordinates = coordinates;
	}

}
