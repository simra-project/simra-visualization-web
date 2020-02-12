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
import visualization.web.resources.serializers.LegResourceMapper;

import java.util.*;

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
    public List<LegResource> getLegsWithin(GeoJsonPoint first, GeoJsonPoint second, GeoJsonPoint third, GeoJsonPoint fourth, Integer minWeight, String day, Double minDist, Double maxDist, Integer startTime, Integer endTime) {
        GeoJsonPolygon polygon = new GeoJsonPolygon(first, second, third, fourth, first);
        List<LegEntity> legEntities = legRepository.findByGeometryWithin(polygon, minWeight);

        if (day != null || startTime != null || endTime != null) {
            long start = System.nanoTime();
            HashSet<String> matching = new HashSet<>();
            if (startTime == null) {
                startTime = 0;
            }
            if (endTime == null) {
                endTime = 24 * 60;
            }
            String identifier = startTime.toString() + "/" + endTime.toString() + "/" + day;
            List<RideEntity> matchingRides = rideRepository.findFilteredRides(null, startTime, endTime, day);
            for (RideEntity ride : matchingRides) {
                matching.add(ride.getId());
            }

            List<LegEntity> legEntitiesFiltered = new ArrayList<>();
            for (LegEntity leg : legEntities) {
                Set<String> matches = new HashSet<>();
                for (String id : leg.getProperties().getFileIdSet()) {
                    if (matching.contains(id)) {
                        matches.add(id);
                    }
                }
                if (!matches.isEmpty()) {
                    leg.getProperties().setFileIdSet(matches);
                    legEntitiesFiltered.add(leg);
                }
            }

            legEntities = legEntitiesFiltered;
        }

        List<String> weekdays = day != null ? Collections.singletonList(day) : new ArrayList<>();
        List<IncidentEntity> incidentEntities = incidentRepository.findFilteredIncidents(polygon, null, null, startTime, endTime, weekdays, new ArrayList<>(), new ArrayList<>(), null, null, null, new ArrayList<>(), null);

        try {
            return legResourceMapper.mapLegEntitiesToResourcesWithIncidents(legEntities, incidentEntities);
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
