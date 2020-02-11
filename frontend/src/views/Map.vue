<template>
    <l-map ref="map"
           :zoom="zoom"
           :center="center"
           :min-zoom="9"
           @update:zoom="zoomUpdated"
           @update:center="centerUpdated"
           @update:bounds="boundsUpdated"
           @click="clickedOnMap($event)">
        <l-tile-layer :url="url" :class="{monochrome: devMonochromeMap}"/>

        <l-control position="topleft">
            <div class="overlay overlay-menu is-hidden-mobile" :class="{ disabled: viewMode === 3 }">
                <b-tabs type="is-toggle" v-model="viewMode" @change="updateUrlQuery">
                    <b-tab-item label="Rides" icon="biking"/>
                    <b-tab-item label="Incidents" icon="car-crash"/>
                    <b-tab-item label="Combined" icon="layer-group"/>
                </b-tabs>

                <b-checkbox v-model="devMonochromeMap" style="margin-bottom: 10px;">Monochrome Map</b-checkbox>

                <MapFilters ref="filters" :view-mode="viewMode" @rides-changed="loadMatchedRoutes" @incidents-changed="loadIncidents" style="margin-top: 12px"/>

                <div v-if="isDebug()">
                    <hr>
                    <strong style="font-size: 16px; margin: -12px 0 4px; display: block;">Debug Settings</strong>
                    <b-checkbox v-model="devMonochromeMap">Monochrome Map</b-checkbox><br>
                    <b-checkbox v-model="devLegPartitions">Legs Partitions <span style="color: #999">(Move map)</span></b-checkbox><br>
                    <b-checkbox v-model="devOverlayIncidents">Overlay Incidents</b-checkbox><br>
                </div>
            </div>
        </l-control>

        <div class="leaflet-control bottomcenter">
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
        </div>

        <l-control position="bottomright">
            <MapLegend :view-mode="viewMode" class="is-hidden-mobile"/>
        </l-control>

        <!-- MapMatched Bike Rides (+ combined view) -->
        <template v-if="viewMode === 0 || viewMode === 2">
            <l-geo-json
                v-if="aggregatetRides"
                :geojson="rides"
                :options="viewMode === 0 ? geoJsonStyleRides : geoJsonStyleCombined"
            />

            <l-geo-json v-if="devOverlayIncidents"
                        v-for="incident in incidents"
                        :key="incident.key"
                        :geojson="incident"
                        :options="geoJsonOptionsMarker">
                <l-popup/>
            </l-geo-json>
        </template>

        <!-- Incident Marker & Incident Heatmap-->
        <template v-else-if="viewMode === 1">
            <Vue2LeafletHeatmap
                v-if="zoom <= heatmapMaxZoom && this.incident_heatmap.length !== 0"
                :lat-lng="incident_heatmap"
                :radius="heatmapRadius"
                :min-opacity="heatmapMinOpacity"
                :max-zoom="10" :blur="heatmapBlur"
                :max="heatmapMaxPointIntensity"/>

            <l-geo-json v-else
                        v-for="incident in incidents"
                        :key="incident.key"
                        :geojson="incident"
                        :options="geoJsonOptionsMarker">
                <l-popup/>
            </l-geo-json>
        </template>

        <!-- Single, highlighted Bike Ride with its Incidents -->
        <template v-else-if="viewMode === 3">
            <l-geo-json :geojson="rideHighlighted" :options="geoJsonStyleHighlight" @ready="highlightedRideLoaded"/>

            <l-marker :lat-lng="rideHighlightStart" :icon="rideHighlightStartIcon">
                <l-tooltip :options="{direction: 'bottom'}"><strong>Start</strong></l-tooltip>
            </l-marker>
            <l-marker :lat-lng="rideHighlightEnd" :icon="rideHighlightEndIcon">
                <l-tooltip :options="{direction: 'bottom'}">
                    <strong>End - {{ (rideHighlighted.properties.distance / 1000).toFixed(1) }} km in {{ rideHighlighted.properties.duration >= 60 ? Math.floor(rideHighlighted.properties.duration / 60) + " h " : "" }}{{ Math.floor(rideHighlighted.properties.duration % 60) }} min</strong>
                </l-tooltip>
            </l-marker>

            <l-geo-json v-for="incident in rideHighlightedIncidents"
                        :key="incident.key"
                        :geojson="incident"
                        :options="geoJsonOptionsMarker">
                <l-popup/>
            </l-geo-json>
        </template>
    </l-map>
</template>

<script>
import Vue from "vue";
import { LCircleMarker, LControl, LGeoJson, LMap, LMarker, LPolyline, LPopup, LTileLayer, LTooltip } from "vue2-leaflet";
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
        LTooltip,
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
            viewMode: parseInt(this.$route.query.m) || 0, // 0 - rides, 1 - incidents, 2 - combined, 3 - highlighted ride
            loadingProgress: null,
            rides: [],
            rideHighlighted: null,
            rideHighlightedIncidents: [],
            rideHighlightStart: [0, 0],
            rideHighlightEnd: [0, 0],
            rideHighlightStartIcon: ExtraMarkers.icon({
                icon: 'fa-biking',
                markerColor: 'white',
                prefix: 'fa'
            }),
            rideHighlightEndIcon: ExtraMarkers.icon({
                icon: 'fa-flag-checkered',
                markerColor: 'black',
                prefix: 'fa'
            }),
            rideMaxWeight: 1,
            rideMaxIncidentWeight: 0.1,
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
            incoming_legs_queue: [],
            loaded_legs: [],
            loaded_legs_strings: [],
            geosearchOptions: {
                provider: new OpenStreetMapProvider(),
            },
            devLegPartitions: false,
            devMonochromeMap: false,
            devOverlayIncidents: false,
            geoJsonStyleRides: {
                style: feature => {
                    if (this.devLegPartitions) return {
                        color: '#' + (0x1000000 + (Math.random()) * 0xffffff).toString(16).substr(1, 6),
                    };

                    return {
                        color: 'hsl(' + (240 - (1 - ((feature.properties.fileIdSet.length - 1) / (this.rideMaxWeight - 1))) * 50) + ', 71%, 53%)',
                        weight: Math.sqrt(feature.properties.fileIdSet.length / this.rideMaxWeight) * 4.5 + 1.5,
                        opacity: 1,
                    };
                },
            },
            geoJsonStyleCombined: {
                style: feature => {
                    let weight = feature.properties.incidentCount / this.rideMaxIncidentWeight;
                    let color = "#84ca50";
                    if (weight > 0.15) color = "#ede202";
                    if (weight > 0.35) color = "#f07d02";
                    if (weight > 0.60) color = "#e60000";
                    if (weight > 0.85) color = "#9e1313";

                    return {
                        color: color,
                        weight: Math.sqrt(feature.properties.fileIdSet.length / this.rideMaxWeight) * 6.5 + 1.5,
                        opacity: 1,
                    };
                },
            },
            geoJsonStyleHighlight: {
                color: "hsl(215, 71%, 53%)",
                weight: 4,
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
                            viewMode: this.viewMode,
                            incident: feature.properties,
                            showRoute: () => {
                                let rideId = feature.properties.rideId;
                                ApiService.loadRide(rideId).then(response => {
                                    let {ride, incidents} = response;

                                    if (ride.status !== 500 && incidents.status !== 500) {
                                        console.log(ride);
                                        console.log(incidents);
                                        ride.properties.duration = Math.floor((ride.properties.ts[ride.properties.ts.length - 1] - ride.properties.ts[0]) / (60 * 1000));

                                        this.rideHighlightStart = [ride.geometry.coordinates[0][1], ride.geometry.coordinates[0][0]];
                                        this.rideHighlightEnd = [ride.geometry.coordinates[ride.geometry.coordinates.length - 1][1], ride.geometry.coordinates[ride.geometry.coordinates.length - 1][0]];
                                        this.rideHighlighted = ride;
                                        this.rideHighlightedIncidents = incidents;
                                        this.viewMode = 3;
                                    } else {
                                        console.log("associated ride is not in db.");
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
        },
        boundsUpdated(bounds) {
            this.bounds = bounds;
            if (this.viewMode === 0 || this.viewMode === 2)
                this.loadMatchedRoutes();
            if (this.viewMode === 1 && (this.zoom > this.heatmapMaxZoom || this.incident_heatmap.length === 0))
                this.loadIncidents();
            console.log(this.incident_heatmap.length);
        },
        updateUrlQuery() {
            this.$router.replace({
                name: "mapQuery",
                params: {
                    lat: this.center.lat,
                    lng: this.center.lng,
                    zoom: this.zoom,
                    viewMode: this.viewMode,
                },
            }).catch(() => {
            });
        },
        highlightedRideLoaded(mapObject) {
            // Fitting ride into view if it's not already
            let rideBounds = mapObject.getBounds().pad(0.1);
            if (!this.bounds.contains(rideBounds)) {
                this.$refs.map.mapObject.flyToBounds(rideBounds);
            }
        },
        unfocusRideHighlight() {
            this.viewMode = 1;
            this.rideHighlightStart = [0, 0];
            this.rideHighlightEnd = [0, 0];
            this.rideHighlighted = null;
            this.rideHighlightedIncidents = [];
            console.log("unfocussing highlighted ride");
        },
        clickedOnMap(event) {
            if (event.originalEvent.target.nodeName !== 'path' && this.rideHighlighted != null) {
                this.unfocusRideHighlight();
            }
        },
        parseRoutes(response, coords) {
            console.log(`testing coords ${coords.toString()}`);
            if (this.incoming_legs_queue.includes(coords.toString())) {
                if (!(this.loaded_legs_strings.includes(coords.toString()))) {
                    this.loaded_legs.push([coords, response]);
                    this.rides.features.push(...response);
                    for (let ride of response) {
                        const weight = ride.properties.fileIdSet.length;
                        if (weight > this.rideMaxWeight)
                            this.rideMaxWeight = weight;

                        const weightIncident = ride.properties.incidentCount;
                        if (weightIncident > this.rideMaxIncidentWeight)
                            this.rideMaxIncidentWeight = weightIncident;
                    }
                } else {
                    console.log("Leg is already loaded.");
                }
            } else {
                console.log("Leg is not in queue.");
                console.log(this.incoming_legs_queue);
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

            let zoom_tmp = this.zoom;
            let sw_lat_tmp = this.bounds._southWest.lat;
            let sw_lng_tmp = this.bounds._southWest.lng;

            setTimeout(() => {
                if (this.zoom === zoom_tmp && sw_lat_tmp === this.bounds._southWest.lat && sw_lng_tmp === this.bounds._southWest.lng) {
                    this.apiWorker.postMessage(["matched", [[min_x, min_y], [max_x, max_y]], Math.max((16 - this.zoom), 1)]);
                } else {
                    console.log(`expected lat ${sw_lat_tmp}, is ${this.bounds._southWest.lat}`);
                    console.log(`expected lng ${sw_lng_tmp}, is ${this.bounds._southWest.lng}`);
                    console.log("--> map motion detected, waiting to get new data.");
                }
            }, 1000);

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
        updateQueue(queue) {
            console.log("updating queue");
            let queue_as_string = [];
            for(let item of queue) {
                queue_as_string.push(item.toString());
            }
            let new_features = [];
            let new_loaded_legs = [];
            let new_loaded_legs_strings = [];
            for(let leg of this.loaded_legs) {
                console.log("testing new leg");
                if (queue_as_string.includes(leg[0].toString())) {
                    console.log("I've seen this leg before!");
                    new_features.push(...leg[1]);
                    new_loaded_legs.push(leg);
                    new_loaded_legs_strings.push(leg[0].toString());
                }
            }
            this.rides = {
                type: "FeatureCollection",
                features: new_features
            };
            this.loaded_legs = new_loaded_legs;
            this.loaded_legs_strings = new_loaded_legs_strings;
            this.incoming_legs_queue = queue_as_string;
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
                    console.log(`message.data[1]`);
                    console.log(message.data[1]);
                    this.parseRoutes(message.data[1], message.data[2]);
                    break;
                case "incidents":
                    this.incidents = message.data[1];
                    break;
                case "queue":
                    this.updateQueue(message.data[1]);
                    break;
            }
        },
        isDebug: () => process.env.VUE_APP_DEBUG === "true",
    },
    async mounted() {
        this.$nextTick(() => {
            this.zoom = this.$refs.map.mapObject.getZoom();
            this.center = this.$refs.map.mapObject.getCenter();
            this.bounds = this.$refs.map.mapObject.getBounds();
            this.loadMatchedRoutes();

            this.zoomUpdated(this.zoom);
            this.centerUpdated(this.center);
            this.boundsUpdated(this.bounds);

            let lat = this.center.lat;
            let lon = this.center.lng;
            console.log(this.center);
            ApiService.loadIncidents(lat, lon).then(response => (this.parseIncidents(response)));
        });

        this.apiWorker = new Worker("/ApiWorker.js");
        this.apiWorker.onmessage = this.handleWorkerMessage;
        this.apiWorker.postMessage(["backendUrl", ApiService.URL_BACKEND]);

    },
};
</script>

<style lang="scss">

    .vue2leaflet-map {
        height: 100%;
        width: 100%;
        flex: 1 0;

        // The class monochrome can only be set on the element before
        .monochrome + .leaflet-pane.leaflet-map-pane .leaflet-pane.leaflet-tile-pane {
            filter: grayscale(1);
        }
    }

    .leaflet-control {
        &.topcenter {
            position: absolute;
            top: 0;
            width: 300px;
            left: calc(50% - 150px);
            right: calc(50% - 150px);
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

            &.overlay-menu {
                &.disabled {
                    pointer-events: none;
                    filter: grayscale(1);
                }

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

                .b-checkbox.checkbox {
                    font-size: 16px;
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

        &.leaflet-control-zoom {
            display: none;
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

        &.extra-marker-circle-white i.fa.fa-biking {
            color: #555 !important;
        }
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
