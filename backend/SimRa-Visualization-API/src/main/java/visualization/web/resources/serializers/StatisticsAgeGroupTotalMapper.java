package visualization.web.resources.serializers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import visualization.web.resources.StatisticsAgeGroupDataResource;

import java.util.ArrayList;

@Component
public class StatisticsAgeGroupTotalMapper {
    public StatisticsAgeGroupDataResource mapAgeGroupTotalToResource(String profileAgeGroupCrossData) throws JsonProcessingException {

        ObjectMapper mapper = new ObjectMapper();
        StatisticsAgeGroupDataResource statisticsAgeGroupTotalMapper = new StatisticsAgeGroupDataResource();

        StatisticsAgeGroupDataResource statisticsAgeGroupDataResource = mapper.readValue(profileAgeGroupCrossData,StatisticsAgeGroupDataResource.class);

        return statisticsAgeGroupDataResource;
    }
}
