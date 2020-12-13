class ApiService {
    static URL_API: string = process.env.VUE_APP_API_URL!;

    static async loadRide(rideId: Number) {
        let ride = fetch(`${ this.URL_API }/api/rides/${ rideId }`).then(r => r.json());
        let incidents = fetch(`${ this.URL_API }/api/incidents?ride_id=${ rideId }`).then(r => r.json());

        // Waiting for both request simultanously
        return {
            ride: (await ride),
            incidents: (await incidents)
        };
    }
}

export { ApiService };
