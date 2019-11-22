package visualization.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import visualization.service.StatisticsService;
import visualization.web.resources.StatisticsResource;

import java.util.ArrayList;
import java.util.List;


/*⁄ø

This is the place where we communicate with the frontend

 */

@RestController
public class StatisticsController {

    @Autowired
    private StatisticsService statisticsService;

    // gets filtered Statistics
    @GetMapping(value = "/statistics")
    public HttpEntity<StatisticsResource> getFilteredStatistics(@RequestParam(value = "from") Long fromTs,
                                                                @RequestParam(value = "until") Long untilTs,
                                                                @RequestParam(value = "bikeTypes") List<Integer> bikeTypes,
                                                                @RequestParam(value = "incidentTypes") List<Integer> incidentTypes,
                                                                @RequestParam(value = "childInvolved") Boolean childInvolved,
                                                                @RequestParam(value = "trailerInvolved") Boolean trailerInvolved,
                                                                @RequestParam(value = "scary") Boolean scary,
                                                                @RequestParam(value = "participants") List<Integer> participants) {

        return ResponseEntity.ok(statisticsService.getFilteredStatistics(fromTs, untilTs, bikeTypes, incidentTypes, childInvolved, trailerInvolved, scary, parseParticipantsList(participants)));
    }

    private List<Boolean> parseParticipantsList(List<Integer> participants) {
        List<Boolean> boolParticipants = new ArrayList<>();
        if (participants.size() > 0) {
            for (int i = 0; i < 10; i++) {
                if (participants.contains(i)) {
                    boolParticipants.add(true);
                } else {
                    boolParticipants.add(false);
                }
            }
        }
        return boolParticipants;
    }
}
