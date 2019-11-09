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
                    <b-switch v-model="showIncidents">Show Incidents</b-switch>
                </div>
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
        <vue2-leaflet-marker-cluster v-if="showIncidents">
            <l-marker v-for="m in markers" :lat-lng="m.latlng">
                <l-popup :content="m.description"></l-popup>
            </l-marker>
        </vue2-leaflet-marker-cluster>
    </l-map>
</template>

<script>
import { LControl, LMap, LMarker, LPolyline, LPopup, LTileLayer } from "vue2-leaflet";
import Vue2LeafletMarkerCluster from "vue2-leaflet-markercluster";
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
        ApiService.loadRoutes().then(routes => this.polylines = routes);
        ApiService.loadIncidents().then(incidents => this.markers = incidents);
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

    @import "~leaflet.markercluster/dist/MarkerCluster.css";
    @import "~leaflet.markercluster/dist/MarkerCluster.Default.css";
</style>
