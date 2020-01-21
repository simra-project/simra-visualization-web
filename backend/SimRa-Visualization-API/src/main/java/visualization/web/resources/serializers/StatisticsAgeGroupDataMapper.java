package visualization.web.resources.serializers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import visualization.web.resources.StatisticsAgeGroupResource;

import java.util.ArrayList;

@Component
public class StatisticsAgeGroupDataMapper {

    public ArrayList<StatisticsAgeGroupResource> mapAgeGroupDataToResource(ArrayList<String> profileAgeGroupCrossData) throws JsonProcessingException {

        ObjectMapper mapper = new ObjectMapper();
        ArrayList<StatisticsAgeGroupResource> statisticsAgeGroupDataMapperArrayList = new ArrayList<>();

        for(String profileAgeGroup:profileAgeGroupCrossData){
           StatisticsAgeGroupResource statisticsAgeGroupResource = mapper.readValue(profileAgeGroup, StatisticsAgeGroupResource.class);
           statisticsAgeGroupDataMapperArrayList.add(statisticsAgeGroupResource);
        }

        return statisticsAgeGroupDataMapperArrayList;
    }
}
