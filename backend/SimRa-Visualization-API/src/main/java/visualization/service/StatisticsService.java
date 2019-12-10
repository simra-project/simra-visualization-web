package visualization.service;

import visualization.web.resources.StatisticsResource;

public interface StatisticsService {

    StatisticsResource getStatisticsByRegion(String region);
}
