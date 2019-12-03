package com.simra.app.csvimporter.filter;

import com.graphhopper.GraphHopper;
import com.graphhopper.PathWrapper;
import com.graphhopper.matching.MapMatching;
import com.graphhopper.matching.MatchResult;
import com.graphhopper.matching.Observation;
import com.graphhopper.reader.osm.GraphHopperOSM;
import com.graphhopper.routing.AlgorithmOptions;
import com.graphhopper.routing.util.FlagEncoder;
import com.graphhopper.routing.util.HintsMap;
import com.graphhopper.routing.util.TraversalMode;
import com.graphhopper.routing.weighting.FastestWeighting;
import com.graphhopper.util.*;
import com.graphhopper.util.shapes.GHPoint;
import com.graphhopper.util.shapes.GHPoint3D;
import com.simra.app.csvimporter.model.RideCSV;
import io.jenetics.jpx.GPX;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class MapMatchingService {

    private MapMatching mapMatching;

    private GraphHopper graphHopper;

    private AlgorithmOptions opts;


    MapMatchingService() {
        CmdArgs graphHopperConfiguration = new CmdArgs();
        graphHopperConfiguration.put("graph.flag_encoders", "bike");
        graphHopperConfiguration.put("datareader.file", "/Users/developer/IdeaProjects/SimRa-Visualization/backend/csvimporter/map-data/Brandenburg_and_Berlin.osm.pbf");
        graphHopper = new GraphHopperOSM().init(graphHopperConfiguration);
        graphHopper.importOrLoad();

        FlagEncoder firstEncoder = graphHopper.getEncodingManager().fetchEdgeEncoders().get(0);
        opts = AlgorithmOptions.start()
                .algorithm(Parameters.Algorithms.DIJKSTRA_BI)
                .traversalMode(TraversalMode.EDGE_BASED)
                .weighting(new FastestWeighting(firstEncoder))
                .maxVisitedNodes(2000)
                .hints(new HintsMap().put("weighting", "fastest")
                        .put("vehicle", "bike"))
                // Penalizing inner-link U-turns only works with fastest weighting, since
                // shortest weighting does not apply penalties to unfavored virtual edges.
                .build();

        mapMatching = new MapMatching(graphHopper, new HintsMap(opts.getHints()));
        mapMatching.setTransitionProbabilityBeta(2.0);
        mapMatching.setMeasurementErrorSigma(50.0);
    }



    private Float currentRouteDistance = 0F;


    public Float getCurrentRouteDistance() {
        return currentRouteDistance;
    }

    public void setCurrentRouteDistance(Float currentRouteDistance) {
        this.currentRouteDistance = currentRouteDistance;
    }

    /**
     * Snaps the GPS Coordinates onto OSM-Streets.
     * TODO Add possibility to snap Routes in other cities than Berlin
     */
    public List<RideCSV> matchToMap(List<RideCSV> rideBeans) {

        GPX gpx = GPX.builder()
                .addTrack(track -> track
                        .addSegment(segment ->
                                rideBeans.forEach(ride ->
                                        segment.addPoint(p -> p.lat(Double.parseDouble(ride.getLat())).lon(Double.parseDouble(ride.getLon()))))))
                .build();

        StopWatch matchSW = new StopWatch();

        Translation tr = new TranslationMap().doImport().getWithFallBack(Locale.GERMANY);


        List<Observation> measurements = gpx.getTracks().get(0).segments().flatMap(segment ->
                segment.points().map(point ->
                        new Observation(new GHPoint(point.getLatitude().doubleValue(), point.getLongitude().doubleValue())))).collect(Collectors.toList());

        matchSW.start();
        MatchResult mr = mapMatching.doWork(measurements);
        matchSW.stop();

        PathWrapper pathWrapper = new PathWrapper();
        new PathMerger(graphHopper.getGraphHopperStorage(), opts.getWeighting()).doWork(pathWrapper, Arrays.asList(mr.getMergedPath()), graphHopper.getEncodingManager(), tr);

        // Copy metainfo of closest (Raw) Point to Snapped Point

        List<RideCSV> result = StreamSupport.stream(pathWrapper.getPoints().spliterator(), false)
                .map(point -> {
                            RideCSV rideCSV = new RideCSV();
                            rideCSV.setLat(String.valueOf(point.lat));
                            rideCSV.setLon(String.valueOf(point.lon));
                            RideCSV nextPoint = findNearestPoint(rideBeans, point);
                            rideCSV.setA(nextPoint.getA());
                            rideCSV.setB(nextPoint.getB());
                            rideCSV.setC(nextPoint.getC());
                            rideCSV.setAcc(nextPoint.getAcc());
                            rideCSV.setTimeStamp(nextPoint.getTimeStamp());
                            rideCSV.setX(nextPoint.getX());
                            rideCSV.setY(nextPoint.getY());
                            rideCSV.setZ(nextPoint.getZ());
                            return rideCSV;
                        }
                ).collect(Collectors.toList());

        currentRouteDistance = (float) pathWrapper.getDistance();

        return result;
    }

    private RideCSV findNearestPoint(List<RideCSV> rawRideCSVList, GHPoint3D matchedPoint) {
        AtomicReference<RideCSV> nextPoint = null;
        AtomicReference<Double> minDist = null;
        rawRideCSVList.forEach(it -> {
            double dist = distance(Double.parseDouble(it.getLat()), Double.parseDouble(it.getLon()), matchedPoint.lat, matchedPoint.lon);
            if (minDist.get() == null || dist < minDist.get()) {
                minDist.set(dist);
                nextPoint.set(it);
            }
        });
        return nextPoint.get();
    }

    private Double distance(Double lat1, Double lon1, Double lat2, Double lon2) {
        final double R = 6371d; // Radius of the earth

        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2) + (Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2));
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return R * c * 1000.0;
    }
}
