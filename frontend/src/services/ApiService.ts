import axios from "axios";

class ApiService {
    static async loadRidesMatched(lat: Number, lon: Number) {
        console.log("http://localhost:8080/ridesMapMatched?lon=" + lon + "&lat=" + lat + "&max=" + 100000);
        return fetch("/ridesMapMatched.json").then(r => r.json());
        // return fetch("http://localhost:8080/ridesMapMatched?lon=" + lon + "&lat=" + lat + "&max=" + 100000).then(r => r.json());
    }

    static async loadRides(lat1: Number, lon1: Number, lat2: Number, lon2: Number) {
        console.log(`http://localhost:8080/rides/area?bottomleft=${lon1},${lat1}&topright=${lon2},${lat2}`);
        return fetch(`http://localhost:8080/rides/area?bottomleft=${lon1},${lat1}&topright=${lon2},${lat2}`).then(r => r.json());
    }

    static async loadIncidents(lat: Number, lon: Number) {
        return fetch("http://localhost:8080/incidents?lon=" + lon + "&lat=" + lat + "&max=" + 100000).then(r => r.json());
    }

    static async startDataProcessing() {
        return (await axios.get("/api/actions/startDataProcessing")).data;
    }
}

export { ApiService };
