class IncidentUtils {
    static getDate(incident: any): string {
        return new Date(incident.rideTimestamp).toLocaleString("en-DE", { timeZone: "Europe/Berlin" });
    }

    static isRecentDate(incident: any): boolean {
        return new Date(incident.rideTimestamp).getFullYear() >= 2010;
    }

    static getType(incident: any): string {
        const incidentTypes = this.getTypes();

        return (incident.incident >= 0 && incident.incident < incidentTypes.length) ? incidentTypes[incident.incident]["translationKey"] : 'incidentTypes.unknown';
    }

    static getParticipant(incident: any): string {
        const participants = this.getParticipants();

        return (incident.iType >= 0 && incident.iType < participants.length) ? participants[incident.iType]["translationKey"] : 'participantTypes.unknown';
    }

    static getBikeType(incident: any) {
        const bikeTypes = this.getBikeTypes();

        return (incident.bikeType > 0 && incident.bikeType < bikeTypes.length) ? bikeTypes[incident.bikeType]["translationKey"] : 'bikeTypes.unknown';
    }

    static getIcon(incident: any): string {
        const participants = this.getParticipants();

        return (incident.iType >= 0 && incident.iType < participants.length) ? participants[incident.iType]["icon"] : "fa-question";
    }

    static getTypes() {
        return [
            { id: 0, translationKey: "incidentTypes.1" },
            { id: 1, translationKey: "incidentTypes.2" },
            { id: 2, translationKey: "incidentTypes.3" },
            { id: 3, translationKey: "incidentTypes.4" },
            { id: 4, translationKey: "incidentTypes.5" },
            { id: 5, translationKey: "incidentTypes.6" },
            { id: 6, translationKey: "incidentTypes.7" },
            { id: 7, translationKey: "incidentTypes.8" },
            { id: 8, translationKey: "incidentTypes.9" },
        ];
    }

    static getParticipants() {
        return [
            { id: 0, translationKey: "participantTypes.unknown", icon: "fa-question" },
            { id: 1, translationKey: "participantTypes.1", icon: "fa-bus" },
            { id: 2, translationKey: "participantTypes.2", icon: "fa-bicycle" },
            { id: 3, translationKey: "participantTypes.3", icon: "fa-walking" },
            { id: 4, translationKey: "participantTypes.4", icon: "fa-shipping-fast" },
            { id: 5, translationKey: "participantTypes.5", icon: "fa-truck-moving" },
            { id: 6, translationKey: "participantTypes.6", icon: "fa-motorcycle" },
            { id: 7, translationKey: "participantTypes.7", icon: "fa-car" },
            { id: 8, translationKey: "participantTypes.8", icon: "fa-taxi" },
            { id: 9, translationKey: "participantTypes.9", icon: "fa-question" },
            { id: 10, translationKey: "participantTypes.10", icon: "fa-bolt" },
            { id: 11, translationKey: "participantTypes.unknown", icon: "fa-question" },
        ];
    }

    static getBikeTypes() {
        return [
            { id: 0, translationKey: "bikeTypes.0" },
            { id: 1, translationKey: "bikeTypes.1" },
            { id: 2, translationKey: "bikeTypes.2" },
            { id: 3, translationKey: "bikeTypes.3" },
            { id: 4, translationKey: "bikeTypes.4" },
            { id: 5, translationKey: "bikeTypes.5" },
            { id: 6, translationKey: "bikeTypes.6" },
            { id: 7, translationKey: "bikeTypes.7" },
            { id: 8, translationKey: "bikeTypes.8" },
        ];
    }

    static participantsToBoolArray(participants: number[]): boolean[] {
        return this.getParticipants().map(a => participants.filter(b => b === a.id).length > 0);
    }
}

export { IncidentUtils };
