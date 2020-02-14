package visualization.service;

import visualization.web.resources.StatisticsResource;

import java.io.IOException;

public interface StatisticsService {

    StatisticsResource getStatisticsByRegion(String region) throws IOException;
}
