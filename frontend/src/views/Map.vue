<template>
    <l-map ref="map"
           :zoom="zoom"
           :center="center"
           :min-zoom="9"
           @update:zoom="zoomUpdated"
           @update:center="centerUpdated"
           @update:bounds="boundsUpdated"
           @click="clickedOnMap($event)">
        <l-tile-layer :url="url"></l-tile-layer>
        <v-geosearch :options="geosearchOptions"></v-geosearch>
        <l-control position="topright">
            <div class="overlay">
                <div class="field">
                    <b-switch v-model="showRides">Show Rides</b-switch>
                </div>
                <div class="field">
                    <b-switch v-model="showIncidents">Show Incidents</b-switch>
                </div>

                <!-- Sliders to fine tune heatmap settings -->
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
                <div>Ride Highlight: {{ rideHighlightId }}</div>
            </div>
        </l-control>
        <l-control position="bottomleft" v-if="rideHighlightContent !== null"> <!-- Using CSS Magic this will appear top-center -->
            <div class="overlay" style="display: flex">
                <div style="flex: 1 0; text-align: center">
                    Showing ride details here... <br> <!-- TODO: Routendetails hier später einfügen -->
                    Length: <strong>{{ rideHighlightContent.length }}</strong>, &nbsp;&nbsp; Duration: <strong>{{ rideHighlightContent.duration }}</strong>
                </div>
                <div style="flex: 0 0; align-self: center">
                    <a class="delete" @click="unfocusRideHighlight"></a>
                </div>
            </div>
        </l-control>

        <!-- Rides-->
        <l-geo-json
            v-if="showRides"
            v-for="ride in rides"
            :geojson="ride.geometry"
            :options="geoJsonOptions"
            @click="clickedOnRide($event, ride)"
        />
        <l-circle-marker v-show="showRides && rideHighlightId !== null" :radius="5" :color="'hsl(171, 100%, 41%)'" :fill-color="'hsl(171, 100%, 41%)'" :fill-opacity="1" :lat-lng="rideHighlightStart"/> <!-- Highlighted Ride start point -->
        <l-circle-marker v-show="showRides && rideHighlightId !== null" :radius="5" :color="'hsl(171, 100%, 41%)'" :fill-color="'hsl(171, 100%, 41%)'" :fill-opacity="1" :lat-lng="rideHighlightEnd"/>   <!-- Highlighted Ride end point -->

        <!--  Incident Markers (with heatmap on higher zoom) -->
        <Vue2LeafletHeatmap
            v-if="zoom <= heatmapMaxZoom && showIncidents"
            :lat-lng="incidentHeatmap"
            :radius="heatmapRadius"
            :min-opacity="heatmapMinOpacity"
            :max-zoom="10" :blur="heatmapBlur"
            :max="heatmapMaxPointIntensity">

        </Vue2LeafletHeatmap>
        <l-geo-json v-for="incident in incidents"
                    v-else-if="showIncidents"
                    :geojson="incident.geometry"
                    :options="geoJsonPopupOptions(incident)">
            <l-popup :content="incident.description"/>
        </l-geo-json>
    </l-map>
</template>

<script>
import { LCircleMarker, LControl, LGeoJson, LMap, LMarker, LPolyline, LPopup, LTileLayer } from "vue2-leaflet";
import Vue2LeafletMarkerCluster from "vue2-leaflet-markercluster";
import Vue2LeafletHeatmap from "../components/Vue2LeafletHeatmap";
import { OpenStreetMapProvider } from "leaflet-geosearch";
import VGeosearch from "vue2-leaflet-geosearch";
import VueSlider from "vue-slider-component";
import "vue-slider-component/theme/default.css";
import { ApiService } from "@/services/ApiService";

export default {
    components: {
        LMap,
        LTileLayer,
        LControl,
        LPolyline,
        Vue2LeafletMarkerCluster,
        LMarker,
        LPopup,
        LCircleMarker,
        Vue2LeafletHeatmap,
        VueSlider,
        VGeosearch,
        LGeoJson,
    },
    data() {
        return {
            url: "http://{s}.tile.osm.org/{z}/{x}/{y}.png",
            zoom: parseInt(this.$route.query.z) || 15,
            center: [this.$route.query.lat || 52.5125322, this.$route.query.lng || 13.3269446],
            bounds: null,
            showRides: true,
            showIncidents: true,
            rides: [],
            rideHighlightId: null,
            rideHighlightContent: null,
            rideHighlightStart: [0, 0],
            rideHighlightEnd: [0, 0],
            incidents: [],
            incidentHeatmap: [],
            heatmapMaxZoom: 15,
            heatmapMinOpacity: 0.75,
            heatmapMaxPointIntensity: 1.0,
            heatmapRadius: 25,
            heatmapBlur: 15,
            geosearchOptions: {
                provider: new OpenStreetMapProvider(),
            },
            geoJsonOptions: {
                style: {
                    color: "hsl(217, 71%, 53%)",
                    weight: 3,
                    opacity: 0.6,
                },
            },
            geoJsonStyleHighlight: {
                // color: 'hsl(0,100%,50%)',
                color: "hsl(171, 100%, 41%)",
                weight: 4,
                opacity: 0.8,
            },
            geoJsonStyleNormal: {
                color: "hsl(217, 71%, 53%)",
                weight: 3,
                opacity: 0.6,
            },
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
            }).catch(() => {});
        },
        clickedOnRide(event, ride) {
            if (this.rideHighlightId != null) this.unfocusRideHighlight();

            // Highlighting this ride
            this.rideHighlightId = ride.properties.rideId;
            this.rideHighlightContent = { length: "10.2 km", duration: "37 min" };

            // Showing start & end point with circles
            this.rideHighlightStart = [ride.geometry.coordinates[0][1], ride.geometry.coordinates[0][0]];
            this.rideHighlightEnd = [ride.geometry.coordinates[ride.geometry.coordinates.length - 1][1], ride.geometry.coordinates[ride.geometry.coordinates.length - 1][0]];

            // Fitting ride into view if it's not already
            let rideBounds = event.target.getBounds().pad(0.1);
            if (!this.bounds.contains(rideBounds)) {
                this.$refs.map.mapObject.flyToBounds(rideBounds);
            }

            event.sourceTarget.setStyle(this.geoJsonStyleHighlight);
            this.rideHighlighted = event.sourceTarget;
        },
        unfocusRideHighlight() {
            this.rideHighlightId = null;
            this.rideHighlightContent = null;
            this.rideHighlightStart = [0, 0];
            this.rideHighlightEnd = [0, 0];
            this.rideHighlighted.setStyle(this.geoJsonStyleNormal);
        },
        clickedOnMap(event) {
            if (event.originalEvent.target.nodeName !== "path" && this.rideHighlightId != null) {
                this.unfocusRideHighlight();
            }
        },
        parseRides(response) {
            this.rides = response;
        },
        parseIncidents(response) {
            this.incidents = response;
            for (let i = 0; i < this.incidents.length; i++) {
                this.incidentHeatmap.push([this.incidents[i].geometry.coordinates[1], this.incidents[i].geometry.coordinates[0], 1]);
            }
        },
        geoJsonPopupOptions(incidentMarker) {
            return {
                onEachFeature: function onEachFeature(feature, layer) {
                    layer.bindPopup(`<table><tr><td>RideId:</td><td>${ incidentMarker.rideId }</td></tr><tr><td>Scary:</td><td>${ incidentMarker.scary }</td></tr></table><p>${ incidentMarker.description }</p>`);
                },
            };
        },
    },
    // Laden der Daten aus der API
    mounted() {
        this.$nextTick(() => {
            this.zoom = this.$refs.map.mapObject.getZoom();
            this.center = this.$refs.map.mapObject.getCenter();
            this.bounds = this.$refs.map.mapObject.getBounds();
        });

        let lat = this.center[0];
        let lon = this.center[1];

        ApiService.loadRides(lat, lon).then(response => (this.parseRides(response)));
        ApiService.loadIncidents(lat, lon).then(response => (this.parseIncidents(response)));
    },
};
</script>

<style lang="scss">

    .vue2leaflet-map {
        height: 100%;
        width: 100%;
        flex: 1 0;
    }

    .leaflet-control-container {
        .leaflet-bottom.leaflet-left {
            top: 0;
            bottom: initial;
            width: 300px;
            left: calc(50% - 150px);
            right: calc(50% - 150px);

            .leaflet-control {
                background-color: orange;
                width: 100%;
                margin: 10px 0 0;
            }
        }
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

    @import "~leaflet.markercluster/dist/MarkerCluster.css";
    @import "~leaflet.markercluster/dist/MarkerCluster.Default.css";
    @import '~leaflet-geosearch/dist/style.css';
    @import '~leaflet-geosearch/assets/css/leaflet.css';

</style>
