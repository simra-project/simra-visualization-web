class IncidentUtils {
    static getDate(incident: any) {
        return new Date(incident.ts).toLocaleString("en-DE", { timeZone: "Europe/Berlin" })
    }

    static getType(incident: any) {
        const incidentTypes = ["Nothing", "Close Pass", "Someone pulling in or out", "Near left or right hook", "Someone approaching head on", "Tailgating", "Near-Dooring", "Dodging an Obstacle", "Other"];

        return (incident.incidentType > 0 && incident.incidentType < incidentTypes.length) ? incidentTypes[incident.incidentType] : "Unknown";
    }

    static getParticipant(incident: any) {
        if (incident.i1Bus) return "Bus";
        if (incident.i2Cyclist) return "Cyclist";
        if (incident.i3Pedestrian) return "Pedestrian";
        if (incident.i4DeliveryVan) return "Delivery Van";
        if (incident.i5Truck) return "Truck";
        if (incident.i6Motorcycle) return "Motorcycle";
        if (incident.i7Car) return "Car";
        if (incident.i8Taxi) return "Taxi";
        if (incident.i9Other) return "Other";
        if (incident.i10EScooter) return "E-Scooter";

        return "Unknown";
    }

    static getIcon(incident: any) {
        if (incident.i1Bus) return "fa-bus";
        if (incident.i2Cyclist) return "fa-bicycle";
        if (incident.i3Pedestrian) return "fa-walking";
        if (incident.i4DeliveryVan) return "fa-shipping-fast";
        if (incident.i5Truck) return "fa-truck-moving";
        if (incident.i6Motorcycle) return "fa-motorcycle";
        if (incident.i7Car) return "fa-car";
        if (incident.i8Taxi) return "fa-taxi";
        if (incident.i10EScooter) return "fa-bolt";

        return "fa-question";
    }
}

export { IncidentUtils };
