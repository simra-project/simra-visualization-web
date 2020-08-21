<template>
    <div>
        <div class="leaflet-control topcenter rides-submode-switcher" v-if="viewMode === config.viewModes.RIDES">
            <b-tabs type="is-toggle-rounded"
                    :value="subViewMode"
                    @change="$emit('update:sub-view-mode', $event)"
                    @update="$emit('update:sub-view-mode', $event)"
            >
                <b-tab-item label="Density" icon="chart-area"></b-tab-item>
                <b-tab-item label="Original Rides" icon="database"></b-tab-item>
            </b-tabs>
        </div>

<!--        <l-geo-json v-if="viewMode === config.viewModes.RIDES && subViewMode === config.subViewModes.RIDES_DENSITY" ref="aggregated_map"-->
<!--                    :geojson="rides"-->
<!--                    :options="styleRides"-->
<!--        />-->

        <l-tile-layer v-if="viewMode === config.viewModes.RIDES && subViewMode === config.subViewModes.RIDES_DENSITY" url="http://207.180.205.80:1337/tiles/simra_rides_density/{z}/{x}/{y}.png"/>
        <l-tile-layer v-if="viewMode === config.viewModes.RIDES && subViewMode === config.subViewModes.RIDES_ORIGINAL" url="http://207.180.205.80:1337/tiles/simra_rides_original/{z}/{x}/{y}.png"/>

        <l-tile-layer v-if="viewMode === config.viewModes.COMBINED" url="http://207.180.205.80:1337/tiles/simra_combined/{z}/{x}/{y}.png"/>
    </div>
</template>

<script>
import { LGeoJson, LTileLayer } from "vue2-leaflet";

import Config from "@/constants";
import { ApiService } from "@/services/ApiService";

export default {
    name: "RideView",
    components: {
        LGeoJson,
        LTileLayer,
    },
    props: {
        zoom: Number,
        bounds: Object,
        viewMode: Number,
        subViewMode: Number,
        getFilters: { default: {} },
    },
    data() {
        return {
            config: Config,
            rides: [],
            rideMaxWeight: 1,
            rideMaxIncidentWeight: 0.1,

            // Loading queue
            incoming_legs_queue: [],
            loaded_legs: [],
            loaded_legs_strings: [],

            // Styles
            styleRides: {
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
            styleCombined: {
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
        }
    },

    methods: {
        loadMatchedRoutes(delayed) {
            let min_y = Math.floor(this.bounds._southWest.lat * 100) - 1;
            let max_y = Math.floor(this.bounds._northEast.lat * 100) + 1;
            let max_x = Math.floor(this.bounds._northEast.lng * 100) + 1;
            let min_x = Math.floor(this.bounds._southWest.lng * 100) - 1;

            let zoom_tmp = this.zoom;
            let sw_lat_tmp = this.bounds._southWest.lat;
            let sw_lng_tmp = this.bounds._southWest.lng;

            setTimeout(() => {
                if (this.zoom === zoom_tmp && sw_lat_tmp === this.bounds._southWest.lat && sw_lng_tmp === this.bounds._southWest.lng) {
                    this.apiWorker.postMessage(["matched", [[min_x, min_y], [max_x, max_y]], Math.max((16 - this.zoom), 1), this.getFilters()]);
                } else {
                    console.log(`expected lat ${sw_lat_tmp}, is ${this.bounds._southWest.lat}`);
                    console.log(`expected lng ${sw_lng_tmp}, is ${this.bounds._southWest.lng}`);
                    console.log("--> map motion detected, waiting to get new data.");
                }
            }, delayed ? 1000 : 0);

            // this.apiWorker.postMessage(["matched", [[this.bounds._southWest.lng * 100, this.bounds._southWest.lat * 100], [this.bounds._northEast.lng * 100, this.bounds._northEast.lat]], 1]);
        },
        parseRoutes(response, coords) {
            console.log(`testing coords ${coords.toString()}`);
            if (this.incoming_legs_queue.includes(coords.toString())) {
                if (!(this.loaded_legs_strings.includes(coords.toString()))) {
                    this.rides.features.push(...response);
                    let curMaxWeight = 1;
                    for (let ride of response) {
                        const weight = ride.properties.fileIdSet.length;
                        if (weight > this.rideMaxWeight)
                            this.rideMaxWeight = weight;
                        if (weight > curMaxWeight)
                            curMaxWeight = weight;
                        const weightIncident = ride.properties.incidentCount;
                        if (weightIncident > this.rideMaxIncidentWeight)
                            this.rideMaxIncidentWeight = weightIncident;
                    }
                    this.loaded_legs.push([coords, response, curMaxWeight]);
                } else {
                    console.log("Leg is already loaded.");
                }
            } else {
                console.log("Leg is not in queue.");
                console.log(this.incoming_legs_queue);
            }
        },
        updateQueue(queue) {
            console.log("updating queue");
            console.log(queue);
            let queue_as_string = [];
            for (let item of queue) {
                queue_as_string.push(item.toString());
            }
            let new_features = [];
            let new_loaded_legs = [];
            let new_loaded_legs_strings = [];
            this.rideMaxWeight = 1;
            for (let leg of this.loaded_legs) {
                console.log("testing new leg");
                if (queue_as_string.includes(leg[0].toString())) {
                    console.log("I've seen this leg before!");
                    new_features.push(...leg[1]);
                    new_loaded_legs.push(leg);
                    new_loaded_legs_strings.push(leg[0].toString());
                    console.log(leg);
                    if (leg[2] > this.rideMaxWeight)
                        this.rideMaxWeight = leg[2]
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
                    this.$emit('on-progress', message.data[1], message.data[2])
                    break;
                case "matched":
                    console.log(`message.data[1]`);
                    console.log(message.data[1]);
                    this.parseRoutes(message.data[1], message.data[2]);
                    break;
                case "queue":
                    this.updateQueue(message.data[1]);
                    break;
            }
        },
    },
    async mounted() {
        this.apiWorker = new Worker("/ApiWorker.js");
        this.apiWorker.onmessage = this.handleWorkerMessage;
        this.apiWorker.postMessage(["backendUrl", ApiService.URL_BACKEND]);
        // this.loadMatchedRoutes(true);
    },
    watch: {
        bounds: function (newValue, oldValue) {
            // if (this.viewMode === Config.viewModes.RIDES && this.subViewMode === Config.subViewModes.RIDES_DENSITY) this.loadMatchedRoutes(true);
        },
        subViewMode: function (newValue, oldValue) {
            // if (newValue === Config.subViewModes.RIDES_DENSITY && newValue !== oldValue) this.loadMatchedRoutes(true);
        },
    }
};
</script>

<style lang="scss">
    .rides-submode-switcher .b-tabs {
        margin-top: 16px;

        li > a {
            background-color: white;
            color: #3273dc;
        }
    }
</style>
