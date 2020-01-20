class IncidentUtils {
    static getDate(incident: any) {
        return new Date(incident.ts).toLocaleString("en-DE", { timeZone: "Europe/Berlin" });
    }

    static getType(incident: any) {
        const incidentTypes = this.getTypes();

        return (incident.incidentType > 0 && incident.incidentType < incidentTypes.length) ? incidentTypes[incident.incidentType]["name"] : "Unknown";
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

    static getTypes() {
        return [
            { id: 0, name: "Nothing" },
            { id: 1, name: "Close Pass" },
            { id: 2, name: "Someone pulling in or out" },
            { id: 3, name: "Near left or right hook" },
            { id: 4, name: "Someone approaching head on" },
            { id: 5, name: "Tailgating" },
            { id: 6, name: "Near-Dooring" },
            { id: 7, name: "Dodging an Obstacle" },
            { id: 8, name: "Other" },
        ];
    }

    static getParticipants() {
        return [
            { id: 0, name: "Bus" },
            { id: 1, name: "Cyclist" },
            { id: 2, name: "Pedestrian" },
            { id: 3, name: "Delivery Van" },
            { id: 4, name: "Truck" },
            { id: 5, name: "Motorcycle" },
            { id: 6, name: "Car" },
            { id: 7, name: "Taxi" },
            { id: 8, name: "Other" },
            { id: 9, name: "E-Scooter" },
            { id: 10, name: "Unknown" },
        ];
    }
}

export { IncidentUtils };
