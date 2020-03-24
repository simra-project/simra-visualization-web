package visualization.service.serializers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.gson.Gson;
import org.springframework.stereotype.Component;
import visualization.web.resources.StatisticsAgeGroupResource;

@Component
public class StatisticsAgeGroupTotalMapper {
    public StatisticsAgeGroupResource mapAgeGroupTotalToResource(String profileAgeGroupCrossData) throws JsonProcessingException {

        //BSON Bug in MongoDB: accumulated & averageDistance have to be cut manually to prevent {"$numberLong": "390103825"} format
        if (profileAgeGroupCrossData.indexOf("\"accumulatedDuration\": ") != -1) {
            int startIndex = profileAgeGroupCrossData.indexOf("\"accumulatedDuration\": ") + 23;
            int endIndex = profileAgeGroupCrossData.indexOf("\"accumulatedSavedCO2\": ") - 2;
            String replacement = profileAgeGroupCrossData.substring(startIndex + 17, endIndex - 2);
            String toBeReplaced = profileAgeGroupCrossData.substring(startIndex, endIndex);
            profileAgeGroupCrossData = profileAgeGroupCrossData.replace(toBeReplaced, replacement);
        }
        if (profileAgeGroupCrossData.indexOf("\"accumulatedDuration\": ") != -1) {
            int startIndex = profileAgeGroupCrossData.indexOf("\"averageDuration\": ") + 19;
            int endIndex = profileAgeGroupCrossData.indexOf("\"averageSavedCO2\": ") - 2;
            String replacement = profileAgeGroupCrossData.substring(startIndex + 17, endIndex - 2);
            String toBeReplaced = profileAgeGroupCrossData.substring(startIndex, endIndex);
            profileAgeGroupCrossData = profileAgeGroupCrossData.replace(toBeReplaced, replacement);
        }
        Gson g = new Gson();
        StatisticsAgeGroupResource statisticsAgeGroupResource = g.fromJson(profileAgeGroupCrossData, StatisticsAgeGroupResource.class);

        return statisticsAgeGroupResource;
    }
}
