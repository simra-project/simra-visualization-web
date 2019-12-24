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
        <l-control position="topright" v-if="false">
            <div class="overlay">
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
        <l-control position="bottomright" v-if="false">
            <div class="overlay overlay-debug">
                <div>Zoom: {{ zoom }}</div>
                <div>Center: {{ center }}</div>
                <div>Bounds: {{ bounds }}</div>
                <div>Ride Highlight: {{ rideHighlightId }}</div>
            </div>
        </l-control>
        <l-control position="topright"> <!-- Using CSS Magic this will appear top-center -->
            <b-tabs type="is-toggle-rounded" v-model="viewMode">
                <b-tab-item label="Bike rides" icon="biking"/>
                <b-tab-item label="Incidents" icon="car-crash"/>
            </b-tabs>
        </l-control>

        <l-control position="bottomleft">
            <div class="overlay overlay-legend">
                <div class="color-box c1"></div>
                <div class="color-box c2"></div>
                <div class="color-box c3"></div>
                <div class="text-box"> Ridership</div>
            </div>
        </l-control>

        <l-control position="bottomleft" v-if="rideHighlightContent !== null && false"> <!-- Using CSS Magic this will appear top-center -->
            <div class="overlay" style="display: flex">
                <div style="flex: 1 0; text-align: center">
                    Showing ride details here... <br> <!-- TODO: Ridedetails hier später einfügen -->
                    Length: <strong>{{ rideHighlightContent.length }}</strong>, &nbsp;&nbsp; Duration: <strong>{{ rideHighlightContent.duration }}</strong>
                </div>
                <div style="flex: 0 0; align-self: center">
                    <a class="delete" @click="unfocusRideHighlight"></a>
                </div>
            </div>
        </l-control>

        <!--    Incident Markers - Stecknadeln, die beim Rauszoomen zusammengefasst werden    -->
        <Vue2LeafletHeatmap
            v-if="zoom <= heatmapMaxZoom && viewMode === 1"
            :lat-lng="incident_heatmap"
            :radius="heatmapRadius"
            :min-opacity="heatmapMinOpacity"
            :max-zoom="10" :blur="heatmapBlur"
            :max="heatmapMaxPointIntensity"/>
        <l-geo-json v-for="incident in incidents"
                    v-else-if="viewMode === 1"
                    :geojson="incident"
                    :options="geoJsonOptionsMarker">
            <l-popup :content="incident.description"/>
        </l-geo-json>

        <!--    Stellt zusammengefasste Rides dar    -->
        <l-geo-json
            v-if="viewMode === 0 && aggregatetRides"
            :geojson="rides"
            :options="geoJsonOptions"
        />

<!--        &lt;!&ndash;    Stellt detaillierte routen da    &ndash;&gt;-->
<!--        <l-geo-json-->
<!--            v-if="showRoutes && !aggregatetRoutes"-->
<!--            v-for="route in detailedRoutes"-->
<!--            :geojson="route"-->
<!--            :options="geoJsonOptionsDetail"-->
<!--            @click="clickedOnRoute($event, route)"-->
<!--        />-->

        <l-circle-marker v-show="viewMode === 0 && rideHighlightId !== null" :radius="5" :color="'hsl(171, 100%, 41%)'" :fill-color="'hsl(171, 100%, 41%)'" :fill-opacity="1" :lat-lng="rideHighlightStart"/> <!-- Highlighted Ride start point -->
        <l-circle-marker v-show="viewMode === 0 && rideHighlightId !== null" :radius="5" :color="'hsl(171, 100%, 41%)'" :fill-color="'hsl(171, 100%, 41%)'" :fill-opacity="1" :lat-lng="rideHighlightEnd"/>   <!-- Highlighted Ride end point -->
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
            viewMode: 0, // 0 - rides, 1 - incidents
            rides: [],
            rideHighlightId: null,
            rideHighlightContent: null,
            rideHighlightStart: [0, 0],
            rideHighlightEnd: [0, 0],
            rideMaxWeight: 1,
            incidents: [],
            incident_heatmap: [],
            heatmapMaxZoom: 15,
            heatmapMinOpacity: 0.75,
            heatmapMaxPointIntensity: 1.0,
            heatmapRadius: 20,
            heatmapBlur: 30,
            aggregatetRides: true,
            detailedAreasLoaded: {},
            detailedRides: [],
            detailedRidesLoaded: {},
            geosearchOptions: {
                provider: new OpenStreetMapProvider(),
            },
            geoJsonOptions: {
                style: feature => {
                    return {
                        // color: 'hsl(' + (217 - (1 - Math.sqrt(feature.properties.weight / this.rideMaxWeight)) * 35) + ', 71%, 53%)',
                        // color: 'hsl(' + (225 - (1 - Math.sqrt(feature.properties.weight / this.rideMaxWeight)) * 43) + ', 71%, 53%)',
                        color: 'hsl(' + (240 - (1 - ((feature.properties.fileIdSet.length - 1) / (this.rideMaxWeight - 1))) * 50) + ', 71%, 53%)',
                        // color: 'hsl(217, 71%, 53%)',
                        weight: Math.sqrt(feature.properties.fileIdSet.length / this.rideMaxWeight) * 4.5 + 1.5,
                        // weight: (Math.log(feature.properties.weight) + 1.25) * 1.25,
                        opacity: 0.75 + Math.sqrt(feature.properties.fileIdSet.length / this.rideMaxWeight) * 0.25,
                    };
                },
            },
            geoJsonOptionsDetail: {
                style: {
                    color: 'hsl(217, 71%, 53%)',
                    weight: 3,
                    opacity: 0.6
                }
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
            geoJsonOptionsMarker: {
                onEachFeature: function onEachFeature(feature, layer) {
                    layer.bindPopup(`<table><tr><td>RideId:</td><td>${ feature.properties.rideId }</td></tr><tr><td>Scary:</td><td>${ feature.properties.scary }</td></tr></table><p>${ feature.properties.description }</p>`);
                }
            }
        };
    },
    methods: {
        zoomUpdated(zoom) {
            this.zoom = zoom;
            this.aggregatetRoutes = zoom < 100;
        },
        centerUpdated(center) {
            this.center = center;
            this.updateUrlQuery();
            if (this.viewMode === 0)
                this.loadMatchedRoutes();
            if (this.viewMode === 1 && this.zoom > this.heatmapMaxZoom)
                this.loadIncidents();
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
            }).catch(() => {
            });
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
            if (event.originalEvent.target.nodeName !== 'path' && this.rideHighlightId != null) {
                this.unfocusRideHighlight();
            }
        },
        parseRoutes(response) {
            this.rides = {
                type: "FeatureCollection",
                features: response
            };
            console.log(`${ this.rides.features.length } ride sections loaded.`);
            for (let ride of this.rides.features) {
                const weight = ride.properties.fileIdSet.length;
                if (weight > this.rideMaxWeight)
                    this.rideMaxWeight = weight;
            }
        },
        parseIncidents(response) {
            for (var i = 0; i < response.length; i++) {
                // console.log(response[i].properties.incidentType);
                // if (response[i].properties.incidentType != 0)
                this.incident_heatmap.push([response[i].geometry.coordinates[1], response[i].geometry.coordinates[0], 1]);
            }
            console.log(this.incident_heatmap);
            console.log("Incident heatmap loaded.");
        },
        loadDetailedRides() {
            // times 100 to avoid floating point errors
            let min_y = Math.floor(this.bounds._southWest.lat * 100) - 1;
            let max_y = Math.floor(this.bounds._northEast.lat * 100) + 1;
            let max_x = Math.floor(this.bounds._northEast.lng * 100) + 1;
            let min_x = Math.floor(this.bounds._southWest.lng * 100) - 1;

            let coords = [];

            for (let y = min_y; y < max_y; y++) {
                for (let x = min_x; x < max_x; x++) {
                    coords.push([x, y]);
                }
            }

            this.apiWorker.postMessage(["routes", coords]);

            console.log(`minx: ${ min_x }, maxx: ${ max_x }`);
            console.log(`miny: ${ min_y }, maxy: ${ max_y }`);
        },

        loadMatchedRoutes() {
            let min_y = Math.floor(this.bounds._southWest.lat * 100) - 1;
            let max_y = Math.floor(this.bounds._northEast.lat * 100) + 1;
            let max_x = Math.floor(this.bounds._northEast.lng * 100) + 1;
            let min_x = Math.floor(this.bounds._southWest.lng * 100) - 1;

            this.apiWorker.postMessage(["matched", [[min_x, min_y], [max_x, max_y]], Math.max((16 - this.zoom), 1)]);
            // this.apiWorker.postMessage(["matched", [[this.bounds._southWest.lng * 100, this.bounds._southWest.lat * 100], [this.bounds._northEast.lng * 100, this.bounds._northEast.lat]], 1]);

        },

        loadIncidents() {
            let min_y = Math.floor(this.bounds._southWest.lat * 100) - 1;
            let max_y = Math.floor(this.bounds._northEast.lat * 100) + 1;
            let max_x = Math.floor(this.bounds._northEast.lng * 100) + 1;
            let min_x = Math.floor(this.bounds._southWest.lng * 100) - 1;

            this.apiWorker.postMessage(["incidents", [[min_x, min_y], [max_x, max_y]]]);

        },
        // loadChunk(x, y) {
        //     if (this.detailedAreasLoaded[`${x},${y}`] == null) {
        //         this.detailedAreasLoaded[`${x},${y}`] = [];
        //         console.log("Started loading new chunk.");
        //         ApiService.loadRoutes(y/100, x/100, (y+1)/100, (x+1)/100).then(response => {
        //             this.detailedAreasLoaded[`${x},${y}`] = response;
        //             response.forEach(route => {
        //                 if (this.detailedRoutesLoaded[route.properties.rideId] == null) {
        //                     this.detailedRoutesLoaded[route.properties.rideId] = route;
        //                     this.detailedRoutes.push(route);
        //                     console.log("added route");
        //                 }
        //             })
        //         })
        //     }
        // },
        handleWorkerMessage(message) {
            switch (message.data[0]) {
                case "routes":
                    console.log(message.data[1]);
                    this.detailedRides.push(...message.data[1]);
                    console.log(this.detailedRides.length + " detailed routes loaded.");
                    break;
                case "matched":
                    this.parseRoutes(message.data[1]);
                    break;
                case "incidents":
                    this.incidents = message.data[1];
                    break;
            }
        }
    },
    // Laden der Daten aus der API
    async mounted() {
        this.$nextTick(() => {
            this.zoom = this.$refs.map.mapObject.getZoom();
            this.center = this.$refs.map.mapObject.getCenter();
            this.bounds = this.$refs.map.mapObject.getBounds();
        });

        let lat = this.center[0];
        let lon = this.center[1];
        // function sleep(ms) {
        //     return new Promise(resolve => setTimeout(resolve, ms));
        // }
        this.apiWorker = new Worker("/ApiWorker.js");
        this.apiWorker.onmessage = this.handleWorkerMessage;
        //
        // this.apiWorker.onmessage = function(event) {
        //     console.log(event.data);
        // };
        //
        // this.apiWorker.postMessage("init");
        // this.apiWorker.postMessage("add");
        //
        // await sleep(1000);
        // this.apiWorker.postMessage("readAll");

        // ApiService.loadRoutesMatched(lat, lon).then(response => (this.parseRoutes(response)));
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
        .leaflet-top.leaflet-right {
            top: 0;
            bottom: initial;
            width: 300px;
            left: calc(50% - 150px);
            right: calc(50% - 150px);

            .leaflet-control {
                width: 100%;
                margin: 10px 0 0;
                display: flex;
                justify-content: center;

                nav.tabs.is-toggle ul {
                    li:not(.is-active) a {
                        background-color: white;
                    }
                }

                section {
                    display: none;
                }
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

        &.overlay-legend {
            padding: 6px 8px;
            border: 1px solid #b5b5b5cc;
            -webkit-box-shadow: none;
            box-shadow: none;

            div {
                display: inline-block;

                & + div {
                    margin-left: 4px;
                }

                &.color-box {
                    width: 15px;
                    height: 15px;
                    margin-bottom: -2.5px;

                    &.c1 {
                        background-color: hsl(190, 71%, 53%);
                        opacity: 0.8;
                    }

                    &.c2 {
                        background-color: hsl(215, 71%, 53%);
                        opacity: 0.9;
                    }

                    &.c3 {
                        background-color: hsl(240, 71%, 53%);
                    }
                }

                &.text-box {
                    font-size: 14px;
                    margin-left: 6px;
                }
            }
        }
    }

    @import "~leaflet.markercluster/dist/MarkerCluster.css";
    @import "~leaflet.markercluster/dist/MarkerCluster.Default.css";
    @import '~leaflet-geosearch/dist/style.css';
    @import '~leaflet-geosearch/assets/css/leaflet.css';

</style>
