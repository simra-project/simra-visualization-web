package visualization.web.resources.serializers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import visualization.web.resources.StatisticsAgeGroupResource;

@Component
public class StatisticsAgeGroupTotalMapper {
    public StatisticsAgeGroupResource mapAgeGroupTotalToResource(String profileAgeGroupCrossData) throws JsonProcessingException {

        ObjectMapper mapper = new ObjectMapper();
        StatisticsAgeGroupResource statisticsAgeGroupTotalMapper = new StatisticsAgeGroupResource();

        StatisticsAgeGroupResource statisticsAgeGroupResource = mapper.readValue(profileAgeGroupCrossData, StatisticsAgeGroupResource.class);

        return statisticsAgeGroupResource;
    }
}
