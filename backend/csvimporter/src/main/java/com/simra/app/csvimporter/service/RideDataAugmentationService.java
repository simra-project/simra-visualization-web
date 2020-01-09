package com.simra.app.csvimporter.service;

import com.mongodb.client.model.geojson.LineString;
import com.mongodb.client.model.geojson.Position;
import com.simra.app.csvimporter.model.RideEntity;
import org.springframework.data.geo.Point;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class RideDataAugmentationService {


    public List<RideEntity> generateRideDataFromMidterm() {


        RideEntity ride1 = new RideEntity();
        RideEntity ride2 = new RideEntity();
        RideEntity ride3 = new RideEntity();

        Point[] array1 = {
                new Point(0d, 1d),
                new Point(1d, 1d),
                new Point(2d, 1d),
                new Point(3d, 1d),
                new Point(4d, 1d),
                new Point(5d, 1d),
                new Point(6d, 1d)
        };
        LineString geoJson1 = new LineString(Arrays.stream(array1).map(it -> new Position(Arrays.asList(it.getX(), it.getY()))).collect(Collectors.toList()));


        Point[] array2 = {
                new Point(0d, 2d),
                new Point(1d, 1d),
                new Point(2d, 1d),
                new Point(3d, 1d),
                new Point(4d, 1d),
                new Point(5d, 2d),
                new Point(6d, 2d)
        };
        LineString geoJson2 = new LineString(Arrays.stream(array2).map(it -> new Position(Arrays.asList(it.getX(), it.getY()))).collect(Collectors.toList()));


        Point[] array3 = {
                new Point(0d, 0d),
                new Point(1d, 0d),
                new Point(2d, 1d),
                new Point(3d, 1d),
                new Point(4d, 1d),
                new Point(5d, 1d),
                new Point(6d, 0d)
        };
        LineString geoJson3 = new LineString(Arrays.stream(array3).map(it -> new Position(Arrays.asList(it.getX(), it.getY()))).collect(Collectors.toList()));


        ride1.setLocationMapMatched(geoJson1);
        ride2.setLocationMapMatched(geoJson2);
        ride3.setLocationMapMatched(geoJson3);

        ride1.setFileId("File1");
        ride2.setFileId("File2");
        ride3.setFileId("File3");

        return Arrays.asList(ride1, ride2, ride3);
    }

    public List<RideEntity> generateRideDataTestCase1() {
        RideEntity ride1 = new RideEntity();
        RideEntity ride2 = new RideEntity();

        Point[] array1 = {
                new Point(1d, 2d),
                new Point(2d, 2d),
                new Point(3d, 2d),
                new Point(4d, 2d)
        };
        LineString geoJson1 = new LineString(Arrays.stream(array1).map(it -> new Position(Arrays.asList(it.getX(), it.getY()))).collect(Collectors.toList()));


        Point[] array2 = {
                new Point(4d, 2d),
                new Point(3d, 2d),
                new Point(2d, 2d),
                new Point(1d, 2d)
        };
        LineString geoJson2 = new LineString(Arrays.stream(array2).map(it -> new Position(Arrays.asList(it.getX(), it.getY()))).collect(Collectors.toList()));

        ride1.setLocationMapMatched(geoJson1);
        ride2.setLocationMapMatched(geoJson2);

        ride1.setFileId("File1");
        ride2.setFileId("File2");

        return Arrays.asList(ride1, ride2);
    }


    public List<RideEntity> generateRideDataTestCase2() {
        RideEntity ride1 = new RideEntity();
        RideEntity ride2 = new RideEntity();

        Point[] array1 = {
                new Point(4d, 4d),
                new Point(3d, 4d),
                new Point(2d, 4d),
                new Point(1d, 3d),
                new Point(1d, 2d),
                new Point(2d, 1d),
                new Point(3d, 1d),
                new Point(4d, 2d),
                new Point(4d, 3d),
                new Point(3d, 4d),
                new Point(3d, 5d)
        };
        LineString geoJson1 = new LineString(Arrays.stream(array1).map(it -> new Position(Arrays.asList(it.getX(), it.getY()))).collect(Collectors.toList()));

        Point[] array2 = {
                new Point(1d, 4d),
                new Point(2d, 4d),
                new Point(1d, 3d),
                new Point(1d, 2d),
                new Point(2d, 1d),
                new Point(1d, 1d)
        };
        LineString geoJson2 = new LineString(Arrays.stream(array2).map(it -> new Position(Arrays.asList(it.getX(), it.getY()))).collect(Collectors.toList()));

        ride1.setLocationMapMatched(geoJson1);
        ride2.setLocationMapMatched(geoJson2);

        ride1.setFileId("File1");
        ride2.setFileId("File2");

        return Arrays.asList(ride1, ride2);
    }


    public List<RideEntity> generateRideDataTestCase3() {
        RideEntity ride1 = new RideEntity();
        RideEntity ride2 = new RideEntity();

        Point[] array1 = {
                new Point(1d, 2d),
                new Point(2d, 2d),
                new Point(3d, 2d),
                new Point(4d, 2d)
        };
        LineString geoJson1 = new LineString(Arrays.stream(array1).map(it -> new Position(Arrays.asList(it.getX(), it.getY()))).collect(Collectors.toList()));

        Point[] array2 = {
                new Point(1d, 1d),
                new Point(2d, 3d),
                new Point(3d, 1d),
                new Point(4d, 3d)
        };
        LineString geoJson2 = new LineString(Arrays.stream(array2).map(it -> new Position(Arrays.asList(it.getX(), it.getY()))).collect(Collectors.toList()));

        ride1.setLocationMapMatched(geoJson1);
        ride2.setLocationMapMatched(geoJson2);

        ride1.setFileId("File1");
        ride2.setFileId("File2");

        return Arrays.asList(ride1, ride2);
    }

    public List<RideEntity> generateRideDataTestCase4() {
        RideEntity ride1 = new RideEntity();
        RideEntity ride2 = new RideEntity();

        Point[] array1 = {
                new Point(1d, 2d),
                new Point(2d, 2d),
                new Point(3d, 2d),
                new Point(4d, 2d)
        };
        LineString geoJson1 = new LineString(Arrays.stream(array1).map(it -> new Position(Arrays.asList(it.getX(), it.getY()))).collect(Collectors.toList()));

        Point[] array2 = {
                new Point(1d, 1d),
                new Point(2d, 2d),
                new Point(2d, 1d),
                new Point(3d, 2d),
                new Point(3d, 1d),
                new Point(4d, 2d),
                new Point(4d, 1d)
        };
        LineString geoJson2 = new LineString(Arrays.stream(array2).map(it -> new Position(Arrays.asList(it.getX(), it.getY()))).collect(Collectors.toList()));

        ride1.setLocationMapMatched(geoJson1);
        ride2.setLocationMapMatched(geoJson2);

        ride1.setFileId("File1");
        ride2.setFileId("File2");

        return Arrays.asList(ride1, ride2);
    }

}
