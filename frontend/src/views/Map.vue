<template>
    <l-map :zoom="zoom"
           :center="center"
           @update:zoom="zoomUpdated"
           @update:center="centerUpdated"
           @update:bounds="boundsUpdated">
        <l-tile-layer :url="url"></l-tile-layer>
        <v-geosearch :options="geosearchOptions"></v-geosearch>
        <l-control position="topright">
            <div class="overlay">
                <p class="subtitle is-5" hidden>Filter</p>
                <div class="field">
                    <b-switch v-model="showTrips">Show Trips</b-switch>
                </div>
                <div class="field">
                    <b-switch v-model="showIncidents">Show Incidents</b-switch>
                </div>

                <!-- Sliders to fine tune heatmap settings.               -->
                MaxZoom
                <vue-slider
                    class="slider"
                    :min="0"
                    :max="18"
                    :interval="1"
                    tooltip="focus"
                    v-model="heatmapMaxZoom">
                </vue-slider>

                Radius
                <vue-slider
                    class="slider"
                    :min="0"
                    :max="100"
                    :interval="1"
                    tooltip="focus"
                    v-model="heatmapRadius">
                </vue-slider>

                Blur
                <vue-slider
                    class="slider"
                    :min="0"
                    :max="100"
                    :interval="1"
                    tooltip="focus"
                    v-model="heatmapBlur">
                </vue-slider>
            </div>
        </l-control>
        <l-control position="bottomright">
            <div class="overlay overlay-debug">
                <div>Zoom: {{ zoom }}</div>
                <div>Center: {{ center }}</div>
                <div>Bounds: {{ bounds }}</div>
            </div>
        </l-control>
        <!--    Stellt eine Route dar    -->
        <l-polyline
            v-if="showTrips"
            v-for="line in polylines"
            :key="line.id"
            :lat-lngs="line.points"
            color="red"
        />
        <!--    Incident Markers - Stecknadeln, die beim Rauszoomen zusammengefasst werden    -->
        <Vue2LeafletHeatmap v-if="zoom<=heatmapMaxZoom&&showAccidents" :lat-lng="incident_heatmap" :radius="heatmapRadius" :min-opacity="heatmapMinOpacity" :max-zoom="10" :blur="heatmapBlur" :max="heatmapMaxPointIntensity"></Vue2LeafletHeatmap>
        <vue2-leaflet-marker-cluster v-else-if="showAccidents">
            <l-marker v-for="m in markers" :lat-lng="m.latlng">
                <l-popup :content="m.description"></l-popup>
            </l-marker>
        </vue2-leaflet-marker-cluster>
    </l-map>
</template>

<script>
import { LControl, LMap, LMarker, LPolyline, LPopup, LTileLayer } from "vue2-leaflet";
import Vue2LeafletMarkerCluster from "vue2-leaflet-markercluster";
import Vue2LeafletHeatmap from "../components/Vue2LeafletHeatmap";
import { OpenStreetMapProvider } from 'leaflet-geosearch';
import VGeosearch from 'vue2-leaflet-geosearch';
import VueSlider from 'vue-slider-component'
import 'vue-slider-component/theme/default.css'
// import { ApiService } from "@/services/ApiService";
import axios from "axios";
import MockAdapter from "axios-mock-adapter";

import 'vue-slider-component/theme/default.css';

// Mock REST API
let mock = new MockAdapter(axios);
mock.onGet("/api/routes").reply(200, {
    routes: [
        {
            id: "route1",
            points: [
                { lat: 52.512641, lng: 13.323587 },
                { lat: 52.512984, lng: 13.328403 },
                { lat: 52.513053, lng: 13.330226 },
                { lat: 52.512568, lng: 13.330560 },
            ],
        },
        {
            id: "route2",
            points: [
                { lat: 52.509599, lng: 13.325507 },
                { lat: 52.510089, lng: 13.324842 },
                { lat: 52.511460, lng: 13.322932 },
                { lat: 52.511930, lng: 13.322465 },
                { lat: 52.512168, lng: 13.322610 },
                { lat: 52.512412, lng: 13.322880 },
                { lat: 52.512882, lng: 13.322869 },
                { lat: 52.513140, lng: 13.322612 },
                { lat: 52.513682, lng: 13.322644 },
                { lat: 52.514505, lng: 13.322574 },
                { lat: 52.514750, lng: 13.322563 },
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

export default {
    components: {
        LMap,
        LTileLayer,
        LControl,
        LPolyline,
        Vue2LeafletMarkerCluster,
        LMarker,
        LPopup,
        Vue2LeafletHeatmap,
        VueSlider,
        VGeosearch
    },
    data() {
        return {
            url: "http://{s}.tile.osm.org/{z}/{x}/{y}.png",
            zoom: parseInt(this.$route.query.z) || 15,
            center: [this.$route.query.lat || 52.5125322, this.$route.query.lng || 13.3269446],
            bounds: null,
            showTrips: true,
            showIncidents: true,
            polylines: [],
            markers: [],
            incident_heatmap: [],
            heatmapMaxZoom: 15,
            heatmapMinOpacity: 0.75,
            heatmapMaxPointIntensity: 1.0,
            heatmapRadius: 25,
            heatmapBlur: 15,
            geosearchOptions: {
                provider: new OpenStreetMapProvider()
            }
        };
    },
    methods: {
        zoomUpdated(zoom) {
            this.zoom = zoom;
        },
        centerUpdated(center) {
            this.center = center;
            this.updateUrlQuery();
        },
        boundsUpdated(bounds) {
            this.bounds = bounds;
        },
        updateUrlQuery() {
            this.$router.replace({
                name: "mapQuery",
                params: {
                    lat: this.center.lat,
                    lng: this.center.lng,
                    zoom: this.zoom,
                },
            });
        },
    },
    // Laden der Daten aus der API
    mounted() {
        axios
            .get("/api/routes")
            .then(
                response => {
                    this.polylines = response.data.routes;
                },
            );
        axios
            .get("/api/markers")
            .then(
                response => {
                    this.markers = response.data.markers;
                    // Workaround to bug in heatmap. Overwriting the array, e.g., arr=[], causes the heatmap to be empty
                    while(this.incident_heatmap.length > 0) {this.incident_heatmap.length.pop();}
                    for (var i = 0; i < this.markers.length; i++) {
                        this.incident_heatmap.push([this.markers[i].latlng.lat, this.markers[i].latlng.lng, 1])
                    }
                }
            );
    },
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

        &.overlay-debug {
            opacity: 0.5;
            transition: opacity 0.5s;
            max-width: 400px;

            &:hover {
                opacity: 1;
            }
        }
    }

    /* Workaround to class selector not working for the geosearch form. */
    div {
        div {
            color: black;
        }
    }


    @import "~leaflet.markercluster/dist/MarkerCluster.css";
    @import "~leaflet.markercluster/dist/MarkerCluster.Default.css";
    @import '~leaflet-geosearch/dist/style.css';
    @import '~leaflet-geosearch/assets/css/leaflet.css';

</style>
