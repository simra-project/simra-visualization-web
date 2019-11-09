import axios from "axios";
import MockAdapter from "axios-mock-adapter";

// Mock REST API
let mock = new MockAdapter(axios);
mock.onGet("/api/routes").reply(200, {
    routes: [
        {
            id: "route1",
            points: [
                {lat: 52.512641, lng: 13.323587},
                {lat: 52.512984, lng: 13.328403},
                {lat: 52.513053, lng: 13.330226},
                {lat: 52.512568, lng: 13.330560},
            ],
        },
        {
            id: "route2",
            points: [
                {lat: 52.509599, lng: 13.325507},
                {lat: 52.510089, lng: 13.324842},
                {lat: 52.511460, lng: 13.322932},
                {lat: 52.511930, lng: 13.322465},
                {lat: 52.512168, lng: 13.322610},
                {lat: 52.512412, lng: 13.322880},
                {lat: 52.512882, lng: 13.322869},
                {lat: 52.513140, lng: 13.322612},
                {lat: 52.513682, lng: 13.322644},
                {lat: 52.514505, lng: 13.322574},
                {lat: 52.514750, lng: 13.322563},
            ],
        },
    ],
});

mock.onGet("/api/markers").reply(200, {
    markers: [
        {
            id: "Incident 1",
            latlng: {
                lat: 52.512830,
                lng: 13.322887,
            },
            description: "Auto hat mich beim Einfaedeln fast mitgenommen!",
        },
        {
            id: "Incident 2",
            latlng: {
                lat: 52.512719,
                lng: 13.324711,
            },
            description: "Wurde von ein paar Vertretern auf ein Jobangebot angesprochen...",
        },
        {
            id: "Incident 3",
            latlng: {
                lat: 52.509777,
                lng: 13.325281,
            },
            description: "Viel zu lange Schlangen in der Mensa.",
        },
    ],
});

class ApiService {
    static async loadRoutes() {
        return (await axios.get("/api/routes")).data.routes;
    }

    static async loadIncidents() {
        return (await axios.get("/api/markers")).data.markers;
    }
}

export { ApiService };
