<template>
    <div>
        <template v-if="rideHighlighted === null">
            <Vue2LeafletHeatmap
                v-if="zoom <= heatmapMaxZoom && incident_heatmap.length !== 0"
                :lat-lng="incident_heatmap"
                :radius="heatmapRadius"
                :min-opacity="heatmapMinOpacity"
                :max-zoom="10" :blur="heatmapBlur"
                :max="heatmapMaxPointIntensity"/>

            <l-geo-json v-else
                        v-for="incident in incidents"
                        :key="incident.key"
                        :geojson="incident"
                        :options="markerStyle">
                <l-popup/>
            </l-geo-json>
        </template>

        <!-- Single, highlighted Bike Ride with its Incidents -->
        <template v-else>
            <l-geo-json :geojson="rideHighlighted" :options="rideHighlightedStyle" @ready="$emit('fit-in-view', $event)"/>

            <l-marker :lat-lng="rideHighlightedStart" :icon="rideHighlightedStartIcon">
                <l-tooltip :options="{direction: 'bottom'}"><strong>Start</strong></l-tooltip>
            </l-marker>
            <l-marker :lat-lng="rideHighlightedEnd" :icon="rideHighlightedEndIcon">
                <l-tooltip :options="{direction: 'bottom'}">
                    <strong>End - {{ (rideHighlighted.properties.distance / 1000).toFixed(1) }} km in {{ rideHighlighted.properties.duration >= 60 ? Math.floor(rideHighlighted.properties.duration / 60) + " h " : "" }}{{ Math.floor(rideHighlighted.properties.duration % 60) }} min</strong>
                </l-tooltip>
            </l-marker>

            <l-geo-json v-for="incident in rideHighlightedIncidents"
                        :key="incident.key"
                        :geojson="incident"
                        :options="markerStyle">
                <l-popup/>
            </l-geo-json>
        </template>
    </div>
</template>

<script>
import Vue from "vue";
import { LGeoJson, LTooltip, LMarker } from "vue2-leaflet";
import Vue2LeafletHeatmap from "@/components/Vue2LeafletHeatmap";
import { ExtraMarkers } from "leaflet-extra-markers";

import { IncidentUtils } from "@/services/IncidentUtils";
import { ApiService } from "@/services/ApiService";
import MapPopup from "@/components/MapPopup";

export default {
    name: "IncidentView",
    components: {
        Vue2LeafletHeatmap,
        LGeoJson,
        LTooltip,
        LMarker,
    },
    props: {
        zoom: Number,
        bounds: Object,
        getFilters: { default: {} },
    },
    data() {
        return {
            incidents: [],

            // Heatmap
            incident_heatmap: [],
            heatmapMaxZoom: 15,
            heatmapMinOpacity: 0.75,
            heatmapMaxPointIntensity: 1.0,
            heatmapRadius: 20,
            heatmapBlur: 30,

            // Markers
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
                            showRoute: () => { this.highlightRide(feature.properties.rideId) },
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
        }
    },
    methods: {
        loadIncidents() {
            console.log("loading incidents");
            let min_y = Math.floor(this.bounds._southWest.lat * 100) - 1;
            let max_y = Math.floor(this.bounds._northEast.lat * 100) + 1;
            let max_x = Math.floor(this.bounds._northEast.lng * 100) + 1;
            let min_x = Math.floor(this.bounds._southWest.lng * 100) - 1;
            console.log([[min_x, min_y], [max_x, max_y]]);

            this.apiWorker.postMessage(["incidents", [[min_x, min_y], [max_x, max_y]], this.getFilters()]);
        },
        processIncidentsForHeatmap() {
            while (this.incident_heatmap.length > 0) {
                this.incident_heatmap.pop();
            }
            for (let i = 0; i < this.incidents.length; i++) {
                this.incident_heatmap.push([this.incidents[i].geometry.coordinates[1], this.incidents[i].geometry.coordinates[0], 1]);
            }
        },
        highlightRide(rideId) {
            ApiService.loadRide(rideId).then(response => {
                let { ride, incidents } = response;

                if (ride.status !== 500 && incidents.status !== 500) {
                    console.log(ride);
                    console.log(incidents);
                    ride.properties.duration = Math.floor((ride.properties.ts[ride.properties.ts.length - 1] - ride.properties.ts[0]) / (60 * 1000));

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
                    this.incidents = message.data[1];
                    this.processIncidentsForHeatmap();
                    break;
            }
        },
    },
    async mounted() {
        this.apiWorker = new Worker("/ApiWorker.js");
        this.apiWorker.onmessage = this.handleWorkerMessage;
        this.apiWorker.postMessage(["backendUrl", ApiService.URL_BACKEND]);
        this.loadIncidents();
    },
    watch: {
        bounds: function (newValue, oldValue) {
            if (this.rideHighlighted === null && (this.zoom > this.heatmapMaxZoom || this.incident_heatmap.length === 0))
                this.loadIncidents();
            console.log(this.incident_heatmap.length);
        }
    }
};
</script>

<style lang="scss">
    @import "~leaflet.markercluster/dist/MarkerCluster.css";
    @import "~leaflet.markercluster/dist/MarkerCluster.Default.css";
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
