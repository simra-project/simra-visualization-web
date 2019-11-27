package visualization.service;

import visualization.web.resources.StatisticsResource;

import java.util.List;

public interface StatisticsService {

    StatisticsResource getStatistics(String region);
    StatisticsResource getStatisticsDebug(String region);
//    StatisticsResource getFilteredStatistics(Long fromTs, Long untilTs, Integer fromMinutesOfDay, Integer untilMinutesOfDay, List<String> weekday, List<Integer> bikeTypes, List<Integer> incidentTypes, Boolean childInvolved, Boolean trailerInvolved, Boolean scary, List<Boolean> participants);

}
