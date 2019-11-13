package visualization.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import visualization.data.mongodb.IncidentRepository;
import visualization.data.mongodb.entities.IncidentEntity;
import visualization.web.resources.IncidentResource;
import visualization.web.resources.RideResource;

import java.util.ArrayList;
import java.util.List;


/*

This is the place where we can do some number crunching and other postprocessing

 */

@Service
public class IncidentServiceImpl implements IncidentService {

    @Autowired
    private IncidentRepository incidentRepository;


    @Override
    public List<IncidentResource> getIncidentsByRideId(int rideId) {

        List<IncidentEntity> incidentEntities = incidentRepository.findByRideId(rideId);
        System.out.println(incidentEntities.toString());

        List<IncidentResource> convertedIncidentResource = new ArrayList();
        for(IncidentEntity incidentEntity:incidentEntities){
            IncidentResource r = new IncidentResource();
            r.setIncidentId(incidentEntity.getKey());
            r.setRideId(incidentEntity.getRideId());
            r.setTimeStamp(incidentEntity.getTimeStamp());
            r.setLatlong(incidentEntity.getMap());
            convertedIncidentResource.add(r);
        }

        return convertedIncidentResource;
    }

}
