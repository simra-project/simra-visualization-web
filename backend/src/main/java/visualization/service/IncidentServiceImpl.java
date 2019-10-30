package visualization.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import visualization.data.mongodb.IncidentRepository;
import visualization.web.resources.RideResource;


/*

This is the place where we can do some number crunching and other postprocessing

 */

@Service
public class IncidentServiceImpl implements IncidentService{

    @Autowired
    private IncidentRepository incidentRepository;

    @Override
    public RideResource getIncidents(){

        RideResource rideResource = incidentRepository.getAllRideIncidents();
        return rideResource;
    }

    @Override
    public RideResource getIncidentsByRideId(int rideId){

        RideResource rideResource = incidentRepository.getRideIncidentsById(rideId);
        return rideResource;
    }


}
