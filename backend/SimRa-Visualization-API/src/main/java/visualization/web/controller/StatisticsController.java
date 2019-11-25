package visualization.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
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

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
public class StatisticsController {

    @Autowired
    private StatisticsService statisticsService;

    // gets filtered Statistics
    @GetMapping(value = "/statistics")
//    public HttpEntity<StatisticsResource> getFilteredStatistics(@RequestParam(value = "fromTs") Long fromTs,
//                                                                @RequestParam(value = "untilTs") Long untilTs,
//                                                                @RequestParam(value = "fromMinutesOfDay") Integer fromMinutesOfDay,
//                                                                @RequestParam(value = "untilMinutesOfDay") Integer untilMinutesOfDay,
//                                                                @RequestParam(value = "weekday") List<String> weekdays,
//                                                                @RequestParam(value = "bikeTypes") List<Integer> bikeTypes,
//                                                                @RequestParam(value = "incidentTypes") List<Integer> incidentTypes,
//                                                                @RequestParam(value = "childInvolved") Boolean childInvolved,
//                                                                @RequestParam(value = "trailerInvolved") Boolean trailerInvolved,
//                                                                @RequestParam(value = "scary") Boolean scary,
//                                                                @RequestParam(value = "participants") List<Integer> participants) {
//
//        return ResponseEntity.ok(statisticsService.getFilteredStatistics(fromTs, untilTs, fromMinutesOfDay, untilMinutesOfDay, weekdays, bikeTypes, incidentTypes, childInvolved, trailerInvolved, scary, parseParticipantsList(participants)));
//    }
    public HttpEntity<StatisticsResource> getStatistics(@RequestParam(value = "region") String region) {
        return ResponseEntity.ok(statisticsService.getStatistics(region));
    }

    @GetMapping(value = "/statisticsDebug")
    public HttpEntity<StatisticsResource> getStatisticsDebug(@RequestParam(value = "region") String region) {
        return ResponseEntity.ok(statisticsService.getStatisticsDebug(region));
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
