package visualization.data.mongodb;

import org.json.JSONObject;
import org.springframework.data.repository.Repository;
import visualization.web.resources.RideResource;


public interface IncidentRepository extends Repository<JSONObject, Integer> {

    RideResource getAllRideIncidents();
    RideResource getRideIncidentsById(int rideId);

}
