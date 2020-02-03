package visualization.data.mongodb;

import org.springframework.data.mongodb.core.geo.GeoJsonPolygon;
import visualization.data.mongodb.entities.IncidentEntity;

import java.util.List;

interface IncidentRepositoryCustom {

    List<IncidentEntity> findFilteredIncidents(GeoJsonPolygon polygon, Long fromTs, Long untilTs, Integer fromMinutesOfDay, Integer untilMinutesOfDay, List<String> weekdays, List<Integer> bikeTypes, List<Integer> incidentTypes, Boolean childInvolved, Boolean trailerInvolved, Boolean scary, List<Boolean> participants, Boolean description);

}
