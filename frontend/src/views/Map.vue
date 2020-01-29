<template>
    <l-map ref="map"
           :zoom="zoom"
           :center="center"
           :options="{ zoomControl: false }"
           @update:zoom="zoomUpdated"
           @update:center="centerUpdated"
           @update:bounds="boundsUpdated"
           @click="clickedOnMap($event)">
        <l-tile-layer :url="url"/>
<!--        <v-geosearch :options="geosearchOptions"/>-->
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
        <l-control position="topleft">
            <div class="overlay overlay-menu">
                <b-tabs type="is-toggle" v-model="viewMode">
                    <b-tab-item label="Bike rides" icon="biking"/>
                    <b-tab-item label="Incidents" icon="car-crash"/>
                </b-tabs>

                <MapFilters ref="filters" :view-mode="viewMode" @rides-changed="loadMatchedRoutes" @incidents-changed="loadIncidents"/>
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
<!--        <l-control position="topcenter" class="topcenter">-->
<!--            <div class="ui-switcher">-->
<!--                <b-tabs type="is-toggle-rounded" v-model="viewMode">-->
<!--                    <b-tab-item label="Bike rides" icon="biking"/>-->
<!--                    <b-tab-item label="Incidents" icon="car-crash"/>-->
<!--                </b-tabs>-->
<!--            </div>-->
<!--        </l-control>-->

        <l-control position="bottomcenter" class="bottomcenter">
            <div class="loading-container" v-if="loadingProgress !== null" :class="{'invisible': loadingProgress === 100}">
                <div class="overlay overlay-loading">
                    <div class="spinner-container">
                        <scaling-squares-spinner
                            :animation-duration="1750"
                            :size="30"
                            color="hsl(217, 71%, 53%)"
                        />

                        <div class="text">Loading Map Data</div>
                    </div>

                    <b-progress type="is-primary is-small" :value="loadingProgress"/>
                </div>
            </div>
        </l-control>

        <l-control position="bottomright">
            <MapLegend :view-mode="viewMode"/>
        </l-control>

        <l-control position="bottomright" v-if="rideHighlightContent !== null"> <!-- Using CSS Magic this will appear top-center -->
            <div class="overlay" style="display: flex">
                <div style="flex: 1 0; text-align: center">
                    Placeholder: More ride details? <br> <!-- TODO: Ridedetails hier später einfügen -->
                    Length: <strong>{{ rideHighlightContent.length }}</strong>, &nbsp;&nbsp; Duration: <strong>{{ rideHighlightContent.duration }}</strong>
                </div>
                <div style="flex: 0 0; align-self: center">
                    <a class="delete" @click="unfocusRideHighlight"/>
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
        <l-geo-json v-else-if="viewMode === 1"
                    v-for="incident in incidents"
                    :key="incident.key"
                    :geojson="incident"
                    :options="geoJsonOptionsMarker">
            <l-popup/>
        </l-geo-json>

        <!--    Stellt zusammengefasste Rides dar    -->
        <l-geo-json
            v-if="viewMode === 0 && aggregatetRides"
            :geojson="rides"
            :options="geoJsonOptions"
        />

        <!--    Stellt detaillierte route da    -->
        <l-geo-json
            v-if="rideHighlighted !== null"
            :geojson="rideHighlighted"
            :options="geoJsonStyleHighlight"
        />

        <l-circle-marker v-show="viewMode === 0 && rideHighlightId !== null" :radius="5" :color="'hsl(2, 100%, 50%)'" :fill-color="'hsl(2, 100%, 50%)'" :fill-opacity="1" :lat-lng="rideHighlightStart"/> <!-- Highlighted Ride start point -->
        <l-circle-marker v-show="viewMode === 0 && rideHighlightId !== null" :radius="5" :color="'hsl(2, 100%, 50%)'" :fill-color="'hsl(2, 100%, 50%)'" :fill-opacity="1" :lat-lng="rideHighlightEnd"/>   <!-- Highlighted Ride end point -->
    </l-map>
</template>

<script>
import Vue from "vue";
import { LCircleMarker, LControl, LGeoJson, LMap, LMarker, LPolyline, LPopup, LTileLayer } from "vue2-leaflet";
import * as L from "leaflet";
import Vue2LeafletMarkerCluster from "vue2-leaflet-markercluster";
import { OpenStreetMapProvider } from "leaflet-geosearch";
import { ExtraMarkers } from "leaflet-extra-markers";
import VGeosearch from "vue2-leaflet-geosearch";
import { ScalingSquaresSpinner } from "epic-spinners";

import MapFilters from "@/components/MapFilters";
import MapLegend from "@/components/MapLegend";
import MapPopup from "@/components/MapPopup";
import Vue2LeafletHeatmap from "@/components/Vue2LeafletHeatmap";
import { ApiService } from "@/services/ApiService";
import { IncidentUtils } from "@/services/IncidentUtils";

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
        VGeosearch,
        LGeoJson,
        ScalingSquaresSpinner,
        MapFilters,
        MapLegend,
    },
    data() {
        return {
            url: "http://{s}.tile.osm.org/{z}/{x}/{y}.png",
            zoom: parseInt(this.$route.query.z) || 15,
            center: [this.$route.query.lat || 52.5125322, this.$route.query.lng || 13.3269446],
            bounds: null,
            viewMode: 0, // 0 - rides, 1 - incidents
            loadingProgress: null,
            rides: [],
            rideHighlightId: null,
            rideHighlightContent: null,
            rideHighlightStart: [0, 0],
            rideHighlightEnd: [0, 0],
            rideHighlighted: null,
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
                // color: "hsl(171, 100%, 41%)",
                color: "hsl(2, 100%, 50%)",
                weight: 4,
                opacity: 0.8,
            },
            geoJsonStyleNormal: {
                color: "hsl(217, 71%, 53%)",
                weight: 3,
                opacity: 0.6,
            },
            geoJsonOptionsMarker: {
                pointToLayer: (feature, latlng) => L.marker(latlng, {
                    icon: ExtraMarkers.icon({
                        icon: IncidentUtils.getIcon(feature.properties),
                        markerColor: feature.properties.scary ? "orange-dark" : "blue",
                        prefix: "fa",
                    }),
                }),
                onEachFeature: (feature, layer) => layer.bindPopup(() => {
                    const mapPopup = new (Vue.extend(MapPopup))({
                        propsData: {
                            incident: feature.properties,
                            showRoute: () => {
                                let rideId = feature.properties.rideId;
                                ApiService.loadRide(rideId).then(ride => {
                                    if (ride.status !== 500) {
                                        this.rideHighlightId = rideId;
                                        console.log(ride);
                                        this.rideHighlightContent = {
                                            length: `${ Math.round(ride.properties.distance) }m`,
                                            duration: "placeholder",
                                        };

                                        this.rideHighlightStart = [ride.geometry.coordinates[0][1], ride.geometry.coordinates[0][0]];
                                        this.rideHighlightEnd = [ride.geometry.coordinates[ride.geometry.coordinates.length - 1][1], ride.geometry.coordinates[ride.geometry.coordinates.length - 1][0]];

                                        // Fitting ride into view if it's not already
                                        this.rideHighlighted = ride;
                                    } else {
                                        console.log("associated ride is not in db.");
                                        this.rideHighlightContent = {
                                            length: "Problem loading ride",
                                            duration: "",
                                        };
                                    }

                                });
                            },
                        },
                    });
                    return mapPopup.$mount().$el;
                }),
            },
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
            if (this.viewMode === 1 && (this.zoom > this.heatmapMaxZoom || this.incident_heatmap.length === 0))
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
            this.rideHighlighted = null;
            console.log("unfocussing highlighted ride");
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

            let filters = this.$refs.filters.getRideFilters();
            console.log(filters);

            this.apiWorker.postMessage(["matched", [[min_x, min_y], [max_x, max_y]], Math.max((16 - this.zoom), 1)]);
            // this.apiWorker.postMessage(["matched", [[this.bounds._southWest.lng * 100, this.bounds._southWest.lat * 100], [this.bounds._northEast.lng * 100, this.bounds._northEast.lat]], 1]);

        },
        loadIncidents() {
            console.log("loading incidents");
            let min_y = Math.floor(this.bounds._southWest.lat * 100) - 1;
            let max_y = Math.floor(this.bounds._northEast.lat * 100) + 1;
            let max_x = Math.floor(this.bounds._northEast.lng * 100) + 1;
            let min_x = Math.floor(this.bounds._southWest.lng * 100) - 1;
            console.log([[min_x, min_y], [max_x, max_y]]);

            let filters = this.$refs.filters.getIncidentFilters();
            console.log(filters);

            this.apiWorker.postMessage(["incidents", [[min_x, min_y], [max_x, max_y]], filters]);
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
        updateLoadingView(progress, expectedTotal) {
            if (expectedTotal === 0) return;
            let currentProgress = Math.min(Math.max(progress / expectedTotal, 0.0), 1.0);

            const minOffset = 0.1;
            this.loadingProgress = Math.min(minOffset + (currentProgress * (1.0 - minOffset)), 1.0) * 100;

            if (progress === expectedTotal) {
                setTimeout(() => this.loadingProgress = null, 1200);
            }
        },
        handleWorkerMessage(message) {
            switch (message.data[0]) {
                case "progress":
                    this.updateLoadingView(message.data[1], message.data[2]);
                    break;
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
    async mounted() {
        this.$nextTick(() => {
            this.zoom = this.$refs.map.mapObject.getZoom();
            this.center = this.$refs.map.mapObject.getCenter();
            this.bounds = this.$refs.map.mapObject.getBounds();

            this.zoomUpdated(this.zoom);
            this.centerUpdated(this.center);
            this.boundsUpdated(this.bounds);
        });

        this.apiWorker = new Worker("/ApiWorker.js");
        this.apiWorker.onmessage = this.handleWorkerMessage;
    },
};
</script>

<style lang="scss">

    .vue2leaflet-map {
        height: 100%;
        width: 100%;
        flex: 1 0;
    }

    .leaflet-control {
        &.topcenter {
            position: absolute;
            top: 0;
            width: 300px;
            left: calc(50% - 150px);
            right: calc(50% - 150px);

            .ui-switcher {
                width: 100%;
                margin: 10px 0 0;
                display: flex;
                justify-content: center;

                nav.tabs.is-toggle ul {
                    li:not(.is-active) a {
                        background-color: white;
                        color: #3273dc;
                    }
                }

                section {
                    display: none;
                }
            }
        }

        &.bottomcenter {
            position: absolute;
            bottom: 0;
            width: 300px;
            left: calc(50% - 150px);
            right: calc(50% - 150px);

            .loading-container {
                $loadingProgressHeight: 0.4rem;

                width: 100%;
                margin: 0 0 calc(12px + #{$loadingProgressHeight});
                display: flex;
                justify-content: center;
                transition: opacity 1s ease 0.2s, filter 1s ease 0.2s;

                &.invisible {
                    opacity: 0;
                    filter: grayscale(.9);

                    .scaling-squares-spinner, .scaling-squares-spinner .square {
                        opacity: 0.6;
                        transition: opacity 1s linear;
                    }
                }

                .overlay {
                    padding: 0;

                    & > div {
                        display: flex;
                        padding: 10px 40px 10px 20px;

                        .scaling-squares-spinner {
                            flex: 0 1 30px;
                        }

                        .text {
                            flex: 1 0;
                            font-size: 16px;
                            align-self: center;
                            padding-left: 20px;
                        }
                    }

                    .progress-wrapper {
                        position: relative;
                        top: $loadingProgressHeight;
                        margin-top: -$loadingProgressHeight;
                        padding: 0;

                        progress {
                            border-radius: 0 0 4px 4px;
                            height: $loadingProgressHeight;
                        }
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

            &.overlay-menu {
                nav.tabs.is-toggle ul {
                    li {
                        flex: 1 0;

                        &:not(.is-active) a {
                            background-color: white;
                            color: #3273dc;
                        }
                    }
                }

                section {
                    display: none;
                }
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
    }

    @import "~leaflet.markercluster/dist/MarkerCluster.css";
    @import "~leaflet.markercluster/dist/MarkerCluster.Default.css";
    @import '~leaflet-geosearch/dist/style.css';
    @import '~leaflet-geosearch/assets/css/leaflet.css';
</style>

<style lang="less">
    @import "~leaflet-extra-markers/src/assets/less/leaflet.extra-markers";

    .extra-marker {
        background-image: url("../assets/markers_custom.png");
    }

    .extra-marker-shadow {
        background-image: url("../assets/markers_shadow.png");
        opacity: 0.75;
    }

    @media (min--moz-device-pixel-ratio: 1.5),(-o-min-device-pixel-ratio: 3/2),
    (-webkit-min-device-pixel-ratio: 1.5),(min-device-pixel-ratio: 1.5),(min-resolution: 1.5dppx) {
        .extra-marker {
            background-image: url("../assets/markers_custom@2x.png");
        }

        .extra-marker-shadow {
            background-image: url("../assets/markers_shadow@2x.png");
        }
    }
</style>
