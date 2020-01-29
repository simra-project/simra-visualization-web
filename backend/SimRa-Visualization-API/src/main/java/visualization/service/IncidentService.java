package visualization.service;

import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import visualization.web.resources.IncidentResource;

import java.util.List;

public interface IncidentService {

    IncidentResource getIncident(String rideid, String key);

    List<IncidentResource> getIncidentsByFileId(String rideid);

    List<IncidentResource> getIncidentsInRange(double latitude, double longitude, int maxDistance);

    List<IncidentResource> getIncidentsInWithin(GeoJsonPoint first, GeoJsonPoint second, GeoJsonPoint third, GeoJsonPoint fourth);

    List<IncidentResource> getFilteredIncidents(Long fromTs, Long untilTs, Integer fromMinutesOfDay, Integer untilMinutesOfDay, String[] weekdays, Integer[] bikeType, Boolean child, Boolean trailer, Integer[] incidentTypes, Boolean[] participants, Boolean scary, Boolean description);
}
