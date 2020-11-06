class IncidentUtils {
    static getDate(incident: any): string {
        return new Date(incident.rideTimestamp).toLocaleString("en-DE", { timeZone: "Europe/Berlin" });
    }

    static isRecentDate(incident: any): boolean {
        return new Date(incident.rideTimestamp).getFullYear() >= 2010;
    }

    static getType(incident: any): string {
        const incidentTypes = this.getTypes();

        return (incident.incident >= 0 && incident.incident < incidentTypes.length) ? incidentTypes[incident.incident]["name"] : "Unknown";
    }

    static getParticipant(incident: any): string {
        const participants = this.getParticipants();

        return (incident.iType >= 0 && incident.iType < participants.length) ? participants[incident.iType]["name"] : "Unknown";
    }

    static getBikeType(incident: any) {
        const bikeTypes = this.getBikeTypes();

        return (incident.bikeType > 0 && incident.bikeType < bikeTypes.length) ? bikeTypes[incident.bikeType]["name"] : "Unknown";
    }

    static getIcon(incident: any): string {
        const participants = this.getParticipants();

        return (incident.iType >= 0 && incident.iType < participants.length) ? participants[incident.iType]["icon"] : "fa-question";
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
            { id: 0, name: "Bus", icon: "fa-bus" },
            { id: 1, name: "Cyclist", icon: "fa-bicycle" },
            { id: 2, name: "Pedestrian", icon: "fa-walking" },
            { id: 3, name: "Delivery Van", icon: "fa-shipping-fast" },
            { id: 4, name: "Truck", icon: "fa-truck-moving" },
            { id: 5, name: "Motorcycle", icon: "fa-motorcycle" },
            { id: 6, name: "Car", icon: "fa-car" },
            { id: 7, name: "Taxi", icon: "fa-taxi" },
            { id: 8, name: "Other", icon: "fa-question" },
            { id: 9, name: "E-Scooter", icon: "fa-bolt" },
            { id: 10, name: "Unknown", icon: "fa-question" },
        ];
    }

    static getBikeTypes() {
        return [
            { id: 0, name: "Not Chosen" },
            { id: 1, name: "City-/Trekking Bike" },
            { id: 2, name: "Road Racing Bike" },
            { id: 3, name: "E-Bike" },
            { id: 4, name: "Recumbent Bicycle" },
            { id: 5, name: "Freight Bicycle" },
            { id: 6, name: "Tandem Bicycle" },
            { id: 7, name: "Mountainbike" },
            { id: 8, name: "Other" },
        ];
    }

    static participantsToBoolArray(participants: number[]): boolean[] {
        return this.getParticipants().map(a => participants.filter(b => b === a.id).length > 0);
    }
}

export { IncidentUtils };
