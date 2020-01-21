package visualization.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import visualization.web.resources.StatisticsResource;

public interface StatisticsService {

    StatisticsResource getStatisticsByRegion(String region) throws JsonProcessingException;
}
