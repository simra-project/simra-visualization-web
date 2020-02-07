package visualization.service;

import org.jetbrains.annotations.NotNull;
import org.opengis.referencing.FactoryException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.core.geo.GeoJsonPolygon;
import org.springframework.stereotype.Service;
import visualization.data.mongodb.IncidentRepository;
import visualization.data.mongodb.LegRepository;
import visualization.data.mongodb.RideRepository;
import visualization.data.mongodb.entities.IncidentEntity;
import visualization.data.mongodb.entities.LegEntity;
import visualization.data.mongodb.entities.RideEntity;
import visualization.web.resources.LegResource;
import visualization.web.resources.RideResource;
import visualization.web.resources.serializers.LegResourceMapper;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class LegServiceImpl implements LegService {

    @Autowired
    private LegRepository legRepository;

    @Autowired
    private RideRepository rideRepository;

    @Autowired
    private IncidentRepository incidentRepository;

    @Autowired
    private LegResourceMapper legResourceMapper;

    @Override
    public List<LegResource> getLegsWithin(GeoJsonPoint first, GeoJsonPoint second, GeoJsonPoint third, GeoJsonPoint fourth, Integer minWeight, String day, Double minDist, Double maxDist) {
        GeoJsonPolygon polygon = new GeoJsonPolygon(first, second, third, fourth, first);
        List<LegEntity> legEntities = legRepository.findByGeometryWithin(polygon, minWeight);

        HashSet<String> matching = new HashSet<String>();
        for (RideEntity ride: rideRepository.findAll()) {
            matching.add(ride.getId());
        }
        if (day != null) {
            List<RideEntity> matchingRides = rideRepository.findByWeekday("Thu");
            matching = updateMatchingSet(matching, matchingRides);
        }

        if (minDist != null || maxDist != null) {
            if (maxDist == null) {
                maxDist = Double.POSITIVE_INFINITY;
            }
            if (minDist == null) {
                minDist = 0.0;
            }
            List<RideEntity> matchingRides = rideRepository.findByDistanceBetween(minDist, maxDist);
            matching = updateMatchingSet(matching, matchingRides);
        }

        List<LegEntity> legEntitiesFiltered = new ArrayList<>();
        for (LegEntity leg: legEntities) {
            Set<String> matches = new HashSet<String>();
            for (String id: leg.getProperties().getFileIdSet()) {
                if (matching.contains(id)) {
                    matches.add(id);
                }
            }
            if (!matches.isEmpty()) {
                leg.getProperties().setFileIdSet(matches);
                legEntitiesFiltered.add(leg);
            }
        }

        List<IncidentEntity> incidentEntities = incidentRepository.findByLocationMapMatchedWithin(new GeoJsonPolygon(first, second, third, fourth, first));

        try {
            return legResourceMapper.mapLegEntitiesToResourcesWithIncidents(legEntitiesFiltered, incidentEntities);
        } catch (FactoryException e) {
            return new ArrayList<>();
        }
    }

    @NotNull
    private HashSet<String> updateMatchingSet(HashSet<String> matching, List<RideEntity> matchingRides) {
        HashSet<String> matching_new = new HashSet<String>();
        for (RideEntity ride: matchingRides) {
            String id = ride.getId();
            if (matching.contains(id)) {
                matching_new.add(id);
            }
        }
        matching = matching_new;
        return matching;
    }
}
