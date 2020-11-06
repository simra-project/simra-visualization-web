class ApiService {
    static URL_BACKEND: string = process.env.VUE_APP_BACKEND_URL!;

    static async loadRide(rideId: Number) {
        let ride = fetch(`http://207.180.205.80:8000/api/rides/${ rideId }`).then(r => r.json());
        let incidents = fetch(`http://207.180.205.80:8000/api/incidents?ride_id=${ rideId }`).then(r => r.json());

        // Waiting for both request simultanously
        return {
            ride: (await ride),
            incidents: (await incidents)
        };
    }
}

export { ApiService };
