package visualization.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import visualization.data.mongodb.StatisticsRepository;
import visualization.data.mongodb.entities.StatisticsEntity;
import visualization.web.resources.StatisticsResource;
import visualization.web.resources.serializers.StatisticsResourceMapper;

@Service
public class StatisticsServiceImpl implements StatisticsService {

    @Autowired
    private StatisticsRepository statisticsRepository;

    @Autowired
    private StatisticsResourceMapper statisticsResourceMapper;

    @Override
    public StatisticsResource getStatisticsByRegion(String region) throws JsonProcessingException {
        StatisticsEntity statisticsEntity = statisticsRepository.findTop1ByRegionOrderByTimestampDesc(region, 0).get();
        return statisticsResourceMapper.mapStatisticsEntityToResource(statisticsEntity);
    }
}
