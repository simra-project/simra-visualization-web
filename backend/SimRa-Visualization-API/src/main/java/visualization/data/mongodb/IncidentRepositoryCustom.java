package visualization.data.mongodb;

import visualization.data.mongodb.entities.IncidentEntity;

import java.util.List;

interface IncidentRepositoryCustom {

    List<IncidentEntity> findFilteredIncidents(Long fromTs, Long untilTs, List<Integer> bikeTypes, List<Integer> incidentTypes, Boolean childInvolved, Boolean trailerInvolved, Boolean scary, List<Boolean> participants);

}
