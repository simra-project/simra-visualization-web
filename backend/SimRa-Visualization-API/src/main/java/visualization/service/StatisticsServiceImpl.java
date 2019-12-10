package visualization.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import visualization.data.mongodb.StatisticsRepository;
import visualization.data.mongodb.entities.StatisticsEntity;
import visualization.web.resources.StatisticsResource;

@Service
public class StatisticsServiceImpl implements StatisticsService {

    @Autowired
    private StatisticsRepository statisticsRepository;

    @Override
    public StatisticsResource getStatisticsByRegion(String region) {
        StatisticsEntity statisticsEntity = statisticsRepository.findByRegion(region).get();
        //TBD
        return null;
    }
}
