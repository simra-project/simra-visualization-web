<template>
    <l-map :zoom="zoom"
           :center="center"
           @update:zoom="zoomUpdated"
           @update:center="centerUpdated"
           @update:bounds="boundsUpdated">
        <l-tile-layer :url="url"></l-tile-layer>
        <l-control position="topright">
            <div class="overlay">
                <p class="subtitle is-5" hidden>Filter</p>
                <div class="field">
                    <b-switch v-model="showTrips">Show Trips</b-switch>
                </div>
                <div class="field">
                    <b-switch v-model="showAccidents">Show Accidents</b-switch>
                </div>
            </div>
        </l-control>
        <!--    Stellt eine Route dar    -->
        <l-polyline
            v-for="line in polylines"
            :key="line.id"
            :lat-lngs="line.points"
            color="red"
        />
        <!--    Incident Markers - Stecknadeln, die beim Rauszoomen zusammengefasst werden    -->
        <vue2-leaflet-marker-cluster>
            <l-marker v-for="m in markers" :lat-lng="m.latlng">
                <l-popup :content="m.description"></l-popup>
            </l-marker>
        </vue2-leaflet-marker-cluster>
    </l-map>
</template>

<script>
import { LMap, LTileLayer, LControl, LPolyline, LMarker, LPopup } from "vue2-leaflet";
import { ToastProgrammatic as Toast } from "buefy";
import axios from "axios";
import MockAdapter from "axios-mock-adapter";
import Vue2LeafletMarkerCluster from "vue2-leaflet-markercluster";

// Mock REST API
let mock = new MockAdapter(axios);
mock.onGet('/api/routes').reply(200, {
    routes: [
        {
            id: "route1",
            points: [
                { lat: 52.512641, lng: 13.323587},
                { lat: 52.512984, lng: 13.328403},
                { lat: 52.513053, lng: 13.330226},
                { lat: 52.512568, lng: 13.330560},
            ],
        },
        {
            id: "route2",
            points: [
                { lat: 52.509599, lng: 13.325507},
                { lat: 52.510089, lng: 13.324842},
                { lat: 52.511460, lng: 13.322932},
                { lat: 52.511930, lng: 13.322465},
                { lat: 52.512168, lng: 13.322610},
                { lat: 52.512412, lng: 13.322880},
                { lat: 52.512882, lng: 13.322869},
                { lat: 52.513140, lng: 13.322612},
                { lat: 52.513682, lng: 13.322644},
                { lat: 52.514505, lng: 13.322574},
                { lat: 52.514750, lng: 13.322563},
            ],
        }
    ]
});

mock.onGet('/api/markers').reply(200, {
    markers: [
        {
            id: "Incident 1",
            latlng: {
                lat: 52.512830,
                lng: 13.322887
            },
            description: "Auto hat mich beim Einfaedeln fast mitgenommen!"
        },
        {
            id: "Incident 2",
            latlng: {
                lat: 52.512719,
                lng: 13.324711
            },
            description: "Wurde von ein paar Vertretern auf ein Jobangebot angesprochen..."
        },
        {
            id: "Incident 3",
            latlng: {
                lat: 52.509777,
                lng: 13.325281
            },
            description: "Viel zu lange Schlangen in der Mensa."
        }
    ]
});

export default {
    components: {
        LMap,
        LTileLayer,
        LControl,
        LPolyline,
        Vue2LeafletMarkerCluster,
        LMarker,
        LPopup
    },
    data () {
        return {
            url: "http://{s}.tile.osm.org/{z}/{x}/{y}.png",
            zoom: 15,
            center: [52.5125322, 13.3269446],
            bounds: null,
            showTrips: true,
            showAccidents: true,
            polylines: [],
            markers: [],
        };
    },
    methods: {
        zoomUpdated (zoom) {
            this.zoom = zoom;
        },
        centerUpdated (center) {
            this.center = center;
        },
        boundsUpdated (bounds) {
            this.bounds = bounds;
        },
        showFakeLoading () {
            let message = "";
            if (this.showTrips) message = "Loading trips ...";
            if (this.showAccidents) message = "Loading accidents ...";
            if (this.showTrips && this.showAccidents) message = "Loading trips and accidents ...";

            if (this.showTrips || this.showAccidents) {
                Toast.open({
                    message: message,
                    position: "is-bottom",
                });
            }
        },
    },
    watch: {
        showTrips (newVal, oldVal) {
            this.showFakeLoading();
        },
        showAccidents (newVal, oldVal) {
            this.showFakeLoading();
        },
    },
    // Laden der Daten aus der API
    mounted() {
        axios
            .get("/api/routes")
            .then(
                response=>{
                    this.polylines = response.data.routes;
                }
            );
        axios
            .get("/api/markers")
            .then(
                response=>{
                    this.markers = response.data.markers;
                }
            );
    }
};
</script>

<style scoped lang="scss">

    .vue2leaflet-map {
        height: 100%;
        width: 100%;
        flex: 1 0;
    }

    .overlay {
        padding: 10px;
        background-color: white;
        -webkit-box-shadow: 0 2px 3px rgba(10, 10, 10, 0.1), 0 0 0 1px rgba(10, 10, 10, 0.1);
        box-shadow: 0 2px 3px rgba(10, 10, 10, 0.1), 0 0 0 1px rgba(10, 10, 10, 0.1);
        color: #4a4a4a;
        position: relative;

        .subtitle {
            color: #4a4a4a;
            margin-bottom: 8px;
        }
    }

    @import "~leaflet.markercluster/dist/MarkerCluster.css";
    @import "~leaflet.markercluster/dist/MarkerCluster.Default.css";
</style>
