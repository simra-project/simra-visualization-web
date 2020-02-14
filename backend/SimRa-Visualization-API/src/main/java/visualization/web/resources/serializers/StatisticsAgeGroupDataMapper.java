package visualization.web.resources.serializers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import org.springframework.stereotype.Component;
import visualization.web.resources.StatisticsAgeGroupResource;

import java.io.IOException;
import java.util.ArrayList;

@Component
public class StatisticsAgeGroupDataMapper {

    public ArrayList<StatisticsAgeGroupResource> mapAgeGroupDataToResource(ArrayList<String> profileAgeGroupCrossData) throws IOException {

        ObjectMapper mapper = new ObjectMapper();
        ArrayList<StatisticsAgeGroupResource> statisticsAgeGroupDataMapperArrayList = new ArrayList<>();


        for (String profileAgeGroup : profileAgeGroupCrossData) {
            if (profileAgeGroup.indexOf("\"accumulatedDuration\": ") != -1) {
                int startIndex = profileAgeGroup.indexOf("\"accumulatedDuration\": ") + 23;
                int endIndex = profileAgeGroup.indexOf("\"accumulatedSavedCO2\": ") - 2;
                String replacement = profileAgeGroup.substring(startIndex + 17, endIndex - 2);
                String toBeReplaced = profileAgeGroup.substring(startIndex, endIndex);
                profileAgeGroup = profileAgeGroup.replace(toBeReplaced, replacement);
            }
            if (profileAgeGroup.indexOf("\"averageDuration\": ") != -1) {
                int startIndex = profileAgeGroup.indexOf("\"averageDuration\": ") + 19;
                int endIndex = profileAgeGroup.indexOf("\"averageSavedCO2\": ") - 2;
                String replacement = profileAgeGroup.substring(startIndex + 17, endIndex - 2);
                String toBeReplaced = profileAgeGroup.substring(startIndex, endIndex);
                profileAgeGroup = profileAgeGroup.replace(toBeReplaced, replacement);
            }

            //final String json = profileAgeGroup.toJson(settings);
            Gson g = new Gson();
            StatisticsAgeGroupResource statisticsAgeGroupResource = g.fromJson(profileAgeGroup, StatisticsAgeGroupResource.class);

            //StatisticsAgeGroupResource statisticsAgeGroupResource = mapper.readValue(profileAgeGroup, StatisticsAgeGroupResource.class);
            statisticsAgeGroupDataMapperArrayList.add(statisticsAgeGroupResource);
        }

        return statisticsAgeGroupDataMapperArrayList;
    }
}
