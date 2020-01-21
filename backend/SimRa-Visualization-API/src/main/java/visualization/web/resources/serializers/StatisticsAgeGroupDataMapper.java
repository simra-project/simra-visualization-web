package visualization.web.resources.serializers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import visualization.data.mongodb.entities.RideEntity;
import visualization.web.resources.RideResource;
import visualization.web.resources.RideResourceProperty;
import visualization.web.resources.StatisticsAgeGroupDataResource;
import visualization.web.resources.StatisticsResource;

import java.util.ArrayList;

@Component
public class StatisticsAgeGroupDataMapper {

    public ArrayList<StatisticsAgeGroupDataResource> mapAgeGroupDataToResource(ArrayList<String> profileAgeGroupCrossData) throws JsonProcessingException {

        ObjectMapper mapper = new ObjectMapper();
        ArrayList<StatisticsAgeGroupDataResource> statisticsAgeGroupDataMapperArrayList = new ArrayList<>();

        for(String profileAgeGroup:profileAgeGroupCrossData){
           StatisticsAgeGroupDataResource statisticsAgeGroupDataResource = mapper.readValue(profileAgeGroup,StatisticsAgeGroupDataResource.class);
           statisticsAgeGroupDataMapperArrayList.add(statisticsAgeGroupDataResource);
        }

        return statisticsAgeGroupDataMapperArrayList;
    }
}
