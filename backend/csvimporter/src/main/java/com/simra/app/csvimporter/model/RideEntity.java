package com.simra.app.csvimporter.model;

import com.mongodb.client.model.geojson.LineString;
import com.mongodb.client.model.geojson.Position;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.Max;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "rides")
public class RideEntity extends RideCSV {

    @Id
    private String id;

    private LineString location;

    private LineString locationMapMatched;

    private ArrayList<Long> tsMapMatched;

    private ArrayList<Long> ts;

    private Float distance;

    private Date addedAt;

    private String weekday;

    private int minuteOfDay;

    @Transient
    private long timeStamp;

    public Date getAddedAt() {
        return addedAt;
    }

    public void setAddedAt(Date addedAt) {
        this.addedAt = addedAt;
    }

    public LineString getLocation() {
        return location;
    }

    public void setLocation(LineString location) {
        this.location = location;
    }

    public ArrayList<Long> getTs() {
        return ts;
    }

    public void setTs(ArrayList<Long> ts) {
        this.ts = ts;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Float getDistance() {
        return distance;
    }

    public void setDistance(Float distance) {
        this.distance = distance;
    }

    public LineString getLocationMapMatched() {
        return locationMapMatched;
    }

    public void setLocationMapMatched(LineString locationMapMatched) {
        this.locationMapMatched = locationMapMatched;
    }

    public ArrayList<Long> getTsMapMatched() {
        return tsMapMatched;
    }

    public void setTsMapMatched(ArrayList<Long> tsMapMatched) {
        this.tsMapMatched = tsMapMatched;
    }

    public String getWeekday() {
        return weekday;
    }

    public void setWeekday(String weekday) {
        this.weekday = weekday;
    }

    public int getMinuteOfDay() {
        return minuteOfDay;
    }

    public void setMinuteOfDay(int minuteOfDay) {
        this.minuteOfDay = minuteOfDay;
    }


    public void setMapMatchedRideBeans(List<RideCSV> mapMatchedRideBeans) {
        ArrayList<Position> coordinates = new ArrayList<>();
        mapMatchedRideBeans.forEach(ride -> {
            List<Double> places = Arrays.asList(Double.parseDouble(ride.getLon()), Double.parseDouble(ride.getLat()));
            Position pos = new Position(places);
            coordinates.add(pos);
        });
        LineString coordinatesMulti = new LineString(coordinates);
        this.setLocationMapMatched(coordinatesMulti);
        ArrayList<Long> ts = new ArrayList<>();
        mapMatchedRideBeans.forEach(ride -> ts.add((ride).getTimeStamp()));
        this.setTsMapMatched(ts);
    }
}
