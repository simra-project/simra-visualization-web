<template>
    <div>
        <!-- Leaflet control map switcher on top of the screen. -->
        <div class="leaflet-control topcenter rides-submode-switcher" v-if="subViewMode === config.subViewModes.POPULARITY_COMBINED || subViewMode === config.subViewModes.POPULARITY_AVOIDED || subViewMode === config.subViewModes.POPULARITY_CHOSEN || subViewMode === config.subViewModes.POPULARITY_SCORE">
            <b-tabs type="is-toggle-rounded"
                    :value="subViewMode"
                    @change="$emit('update:sub-view-mode', $event)"
                    @update="$emit('update:sub-view-mode', $event)"
            >
                <b-tab-item :label="$t('popularity.combined')" icon="chart-area"></b-tab-item>
                <b-tab-item :label="$t('popularity.avoided')" icon="database"></b-tab-item>
                <b-tab-item :label="$t('popularity.chosen')" icon="database"></b-tab-item>
                <b-tab-item :label="$t('popularity.score')" icon="chart-area"></b-tab-item>
            </b-tabs>
        </div>

        <!-- When no specific ride is highlighted -->
        <template v-if="rideHighlighted === null">
            <!-- List of sub view layers. -->
            <l-tile-layer
                v-if="subViewMode === config.subViewModes.POPULARITY_COMBINED"
                :url="TILE_URL + '/tiles/popularity-combined/{z}/{x}/{y}.png'"
            />
            <l-tile-layer
                v-if="subViewMode === config.subViewModes.POPULARITY_AVOIDED"
                :url="TILE_URL + '/tiles/popularity-original_avoided/{z}/{x}/{y}.png'"
            />
            <l-tile-layer
                v-if="subViewMode === config.subViewModes.POPULARITY_CHOSEN"
                :url="TILE_URL + '/tiles/popularity-original_chosen/{z}/{x}/{y}.png'"
            />
            <l-tile-layer
                v-if="subViewMode === config.subViewModes.POPULARITY_SCORE"
                :url="TILE_URL + '/tiles/popularity-score/{z}/{x}/{y}.png'"
            />

            <!-- Show all incidents -->
            <l-geo-json v-if="zoom > 15 && incidents && incidentsVisible"
                    :geojson="incidents"
                    :options="markerStyle">
                <l-popup/>
            </l-geo-json>
        </template>

        <!-- Single, highlighted Bike Ride with its Incidents -->
        <template v-else>
            <l-geo-json :geojson="rideHighlighted" :options="rideHighlightedStyle" @ready="$emit('fit-in-view', $event)"/>

            <l-marker :lat-lng="rideHighlightedStart" :icon="rideHighlightedStartIcon">
                <l-tooltip :options="{direction: 'bottom'}"><strong>{{ $t('incident.start') }}</strong></l-tooltip>
            </l-marker>
            <l-marker :lat-lng="rideHighlightedEnd" :icon="rideHighlightedEndIcon">
                <l-tooltip :options="{direction: 'bottom'}"><strong>{{ $t('incident.end') }}</strong></l-tooltip>
            </l-marker>

            <l-geo-json v-if="rideHighlightedIncidents" :geojson="rideHighlightedIncidents" :options="markerStyle">
                <l-popup/>
            </l-geo-json>
        </template>
    </div>
</template>

<script>
import Vue from "vue";
import { LGeoJson, LTileLayer, LMarker, LTooltip } from "vue2-leaflet";
import { ExtraMarkers } from "leaflet-extra-markers";

import { IncidentUtils } from "@/services/IncidentUtils";
import { ApiService } from "@/services/ApiService";
import MapPopup from "@/components/MapPopup";

import Config from "@/constants";

export default {
    name: "PopularityView",
    components: {
        LGeoJson,
        LTooltip,
        LMarker,
        LTileLayer
    },
    props: {
        subViewMode: Number,
        zoom: Number,
        bounds: Object,
        incidentsVisible: Boolean
    },
    data() {
        return {
            config: Config,

            // Markers
            incidents: [],
            incidentRequestKey: null,
            markerStyle: {
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
                            showRouteEnabled: this.rideHighlighted === null,
                            showRoute: () => { this.highlightRide(feature.properties.ride_id) },
                            t: (key) => { return this.$t(key) },
                        },
                    });
                    return mapPopup.$mount().$el;
                }),
            },

            // Highlighted Ride
            rideHighlighted: null,
            rideHighlightedIncidents: [],
            rideHighlightedStart: [0, 0],
            rideHighlightedEnd: [0, 0],
            rideHighlightedStartIcon: ExtraMarkers.icon({
                icon: 'fa-biking',
                markerColor: 'white',
                prefix: 'fa'
            }),
            rideHighlightedEndIcon: ExtraMarkers.icon({
                icon: 'fa-flag-checkered',
                markerColor: 'black',
                prefix: 'fa'
            }),
            rideHighlightedStyle: {
                color: "hsl(215, 71%, 53%)",
                weight: 4,
            },
        };
    },
    computed: {
        TILE_URL() {
            return process.env.VUE_APP_TILE_URL;
        }
    },
    methods: {
        loadIncidents() {
            console.log("loading incidents");
            let min_y = Math.floor(this.bounds._southWest.lat * 100) - 1;
            let max_y = Math.floor(this.bounds._northEast.lat * 100) + 1;
            let max_x = Math.floor(this.bounds._northEast.lng * 100) + 1;
            let min_x = Math.floor(this.bounds._southWest.lng * 100) - 1;
            let bottomLeft = [min_x, min_y];
            let bottomRight = [max_x, min_y];
            let topLeft = [min_x, max_y];
            let topRight = [max_x, max_y];
            let polygon = [bottomLeft, bottomRight, topRight, topLeft, bottomLeft];
            console.log(polygon);

            this.apiWorker.postMessage(["incidents", polygon]);
        },
        highlightRide(rideId) {
            ApiService.loadRide(rideId).then(response => {
                let { ride, incidents } = response;

                if (ride.status !== 500 && incidents.status !== 500) {
                    console.log(ride);
                    console.log(incidents);

                    this.rideHighlightedStart = [ride.geometry.coordinates[0][1], ride.geometry.coordinates[0][0]];
                    this.rideHighlightedEnd = [ride.geometry.coordinates[ride.geometry.coordinates.length - 1][1], ride.geometry.coordinates[ride.geometry.coordinates.length - 1][0]];
                    this.rideHighlighted = ride;
                    this.rideHighlightedIncidents = incidents;
                } else {
                    console.log("associated ride is not in db.");
                }
            });
        },
        resetRideHighlight() {
            this.rideHighlightedStart = [0, 0];
            this.rideHighlightedEnd = [0, 0];
            this.rideHighlighted = null;
            this.rideHighlightedIncidents = [];
            console.log("unfocussing highlighted ride");
        },
        clickedOnMap(event) {
            if (event.originalEvent.target.nodeName !== 'path' && this.rideHighlighted != null) {
                this.resetRideHighlight();
            }
        },
        handleWorkerMessage(message) {
            switch (message.data[0]) {
                case "progress":
                    this.$emit('on-progress', message.data[1], message.data[2])
                    break;
                case "incidents":
                    if (this.incidentRequestKey === null || this.incidentRequestKey !== message.data[1]) {
                        console.log("Setting incident variable with new data");
                        this.incidentRequestKey = message.data[1];
                        this.incidents = message.data[2];
                    }
                    break;
            }
        },
    },
    async mounted() {
        this.apiWorker = new Worker("/ApiWorker.js");
        this.apiWorker.onmessage = this.handleWorkerMessage;
        this.apiWorker.postMessage(["backendUrl", ApiService.URL_API]);
    },
    watch: {
        bounds: function (newValue, oldValue) {
            if (this.zoom > 15)
                this.loadIncidents();
        },
        incidentsVisible() {
            return this.incidentsVisible;
        }
    }
};
</script>

<style lang="scss">
@import "~leaflet.markercluster/dist/MarkerCluster.css";
@import "~leaflet.markercluster/dist/MarkerCluster.Default.css";

.rides-submode-switcher .b-tabs {
    margin-top: 16px;
    position: absolute;
    width: 500px;
    left: calc(50% - 250px);
    right: calc(50% - 250px);

    li > a {
        background-color: white;
        color: #3273dc;
    }
}
</style>

<style lang="less">
    @import "~leaflet-extra-markers/src/assets/less/leaflet.extra-markers";

    .extra-marker {
        background-image: url("../../assets/markers_custom.png");

        &.extra-marker-circle-white i.fa.fa-biking {
            color: #555 !important;
        }
    }

    .extra-marker-shadow {
        background-image: url("../../assets/markers_shadow.png");
        opacity: 0.75;
    }

    @media (min--moz-device-pixel-ratio: 1.5),(-o-min-device-pixel-ratio: 3/2),
    (-webkit-min-device-pixel-ratio: 1.5),(min-device-pixel-ratio: 1.5),(min-resolution: 1.5dppx) {
        .extra-marker {
            background-image: url("../../assets/markers_custom@2x.png");
        }

        .extra-marker-shadow {
            background-image: url("../../assets/markers_shadow@2x.png");
        }
    }
</style>
