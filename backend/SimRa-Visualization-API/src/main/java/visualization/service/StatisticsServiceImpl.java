package visualization.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import visualization.data.mongodb.IncidentRepository;
import visualization.data.mongodb.RideRepository;
import visualization.data.mongodb.entities.IncidentEntity;
import visualization.data.mongodb.entities.RideEntity;
import visualization.web.resources.IncidentStatisticsResource;
import visualization.web.resources.RideStatisticsResource;
import visualization.web.resources.StatisticsResource;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


/*

This is the place where we can do some number crunching and other postprocessing for Statistics

 */
@Service
public class StatisticsServiceImpl implements StatisticsService {

    @Autowired
    private RideRepository rideRepository;

    @Autowired
    private IncidentRepository incidentRepository;

    @Override
    public StatisticsResource getStatistics() {
        StatisticsResource statsResource = new StatisticsResource();
        List rides = rideRepository.findAll();
        statsResource.getRidesStatistics().addAll((ArrayList) rides.stream().map(it -> parseRideMetaData((RideEntity) it)).collect(Collectors.toList()));

        List incidents = incidentRepository.findAll();
        statsResource.getIncidentsStatistics().addAll((ArrayList) incidents.stream().map(it -> parseIncidentMetaData((IncidentEntity) it)).collect(Collectors.toList()));

        return statsResource;
    }

    private RideStatisticsResource parseRideMetaData(RideEntity ride) {
        RideStatisticsResource rideStatisticsResource = new RideStatisticsResource();
        rideStatisticsResource.setStartTime(ride.getTs().get(0));
        rideStatisticsResource.setDuration((int) (ride.getTs().get(ride.getTs().size() - 1) - ride.getTs().get(0)));
        rideStatisticsResource.setLength(ride.getLength());
        rideStatisticsResource.setSavedCO2(ride.getLength() * 0.138F);
        // parse more Stats Metadata for Rides here
        //..
        return rideStatisticsResource;
    }

    private IncidentStatisticsResource parseIncidentMetaData(IncidentEntity incident) {
        IncidentStatisticsResource incidentStatisticsResource = new IncidentStatisticsResource();
        incidentStatisticsResource.setBikeType(incident.getBike());
        incidentStatisticsResource.setTs(incident.getTimestamp());
        incidentStatisticsResource.setChildInvolved(incident.getChildCheckBox());
        incidentStatisticsResource.setTrailerInvolved(incident.getTrailerCheckBox());
        incidentStatisticsResource.setIncidentType(incident.getIncident());
        incidentStatisticsResource.setScary(incident.getScary());
        incidentStatisticsResource.setI1Bus(incident.getI1());
        incidentStatisticsResource.setI2Cyclist(incident.getI2());
        incidentStatisticsResource.setI3Pedestrian(incident.getI3());
        incidentStatisticsResource.setI4DeliveryVan(incident.getI4());
        incidentStatisticsResource.setI5Truck(incident.getI5());
        incidentStatisticsResource.setI6Motorcycle(incident.getI6());
        incidentStatisticsResource.setI7Car(incident.getI7());
        incidentStatisticsResource.setI8Taxi(incident.getI8());
        incidentStatisticsResource.setI9Other(incident.getI9());
        incidentStatisticsResource.setI10EScooter(incident.getI10());
        return incidentStatisticsResource;
    }

}
