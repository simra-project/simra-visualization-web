package visualization.service;

import visualization.web.resources.StatisticsResource;

import java.util.List;

public interface StatisticsService {

    StatisticsResource getFilteredStatistics(Long fromTs, Long untilTs, List<Integer> bikeTypes, List<Integer> incidentTypes, Boolean childInvolved, Boolean trailerInvolved, Boolean scary, List<Boolean> participants);

}
