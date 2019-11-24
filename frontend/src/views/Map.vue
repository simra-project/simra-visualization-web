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
                    <b-switch v-model="showRoutes">Show Trips</b-switch>
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
        <l-control position="bottomleft" v-if="routeHighlightContent !== null"> <!-- Using CSS Magic this will appear top-center -->
            <div class="overlay" style="display: flex">
                <div style="flex: 1 0; text-align: center">
                    Showing route details here... <br> <!-- TODO: Routendetails hier später einfügen -->
                    Length: <strong>{{ routeHighlightContent.length }}</strong>, &nbsp;&nbsp; Duration: <strong>{{ routeHighlightContent.duration }}</strong>
                </div>
                <div style="flex: 0 0; align-self: center">
                    <a class="delete" @click="unfocusRouteHighlight"></a>
                </div>
            </div>
        </l-control>

        <!--    Stellt eine Route dar    -->
        <l-geo-json
            v-if="showRoutes"
            v-for="route in routes"
            :geojson="route.coordinates"
            :options="geoJsonOptions"
            @click="clickedOnRoute($event, route)"
        />
        <l-circle-marker v-show="showRoutes && routeHighlightId !== null" :radius="5" :color="'hsl(171, 100%, 41%)'" :fill-color="'hsl(171, 100%, 41%)'" :fill-opacity="1" :lat-lng="routeHighlightStart"/> <!-- Highlighted Route start point -->
        <l-circle-marker v-show="showRoutes && routeHighlightId !== null" :radius="5" :color="'hsl(171, 100%, 41%)'" :fill-color="'hsl(171, 100%, 41%)'" :fill-opacity="1" :lat-lng="routeHighlightEnd"/>   <!-- Highlighted Route end point -->

        <!--    Incident Markers - Stecknadeln, die beim Rauszoomen zusammengefasst werden    -->
        <Vue2LeafletHeatmap
            v-if="zoom <= heatmapMaxZoom && showIncidents"
            :lat-lng="incident_heatmap"
            :radius="heatmapRadius"
            :min-opacity="heatmapMinOpacity"
            :max-zoom="10" :blur="heatmapBlur"
            :max="heatmapMaxPointIntensity">

        </Vue2LeafletHeatmap>
        <l-geo-json v-for="marker in markers"
                    v-else-if="showIncidents"
                    :geojson="marker.coordinates"
                    :options="geoJsonPopupOptions(marker)">
            <l-popup :content="marker.description"></l-popup>
        </l-geo-json>
    </l-map>
</template>

<script>
    import { LControl, LMap, LMarker, LPolyline, LPopup, LTileLayer, LCircleMarker, LGeoJson } from "vue2-leaflet";
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
                showRoutes: true,
                showIncidents: true,
                routes: [],
                routeHighlightId: null,
                routeHighlightContent: null,
                routeHighlightStart: [0, 0],
                routeHighlightEnd: [0, 0],
                markers: [],
                incident_heatmap: [],
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
                        color: 'hsl(217, 71%, 53%)',
                        weight: 3,
                        opacity: 0.6
                    },
                },
                geoJsonStyleHighlight: {
                    color: 'hsl(0,100%,50%)',
                    weight: 4,
                    opacity: 0.8
                },
                geoJsonStyleNormal: {
                    color: 'hsl(217, 71%, 53%)',
                    weight: 3,
                    opacity: 0.6
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
            clickedOnRoute(event, route) {
                // Highlighting this route
                this.routeHighlightId = route.rideId;
                this.routeHighlightContent = { length: "10.2 km", duration: "37 min" };

                // Showing start & end point with circles
                this.routeHighlightStart = [route.coordinates.coordinates[0][1], route.coordinates.coordinates[0][0]];
                this.routeHighlightEnd = [route.coordinates.coordinates[route.coordinates.coordinates.length - 1][1], route.coordinates.coordinates[route.coordinates.coordinates.length - 1][0]];

                // Fitting route into view if it's not already
                let routeBounds = event.target.getBounds().pad(0.1);
                if (!this.bounds.contains(routeBounds)) {
                    this.$refs.map.mapObject.flyToBounds(routeBounds);
                }

                event.sourceTarget.setStyle(this.geoJsonStyleHighlight);
                this.routeHighlighted = event.sourceTarget;
            },
            unfocusRouteHighlight() {
                this.routeHighlightId = null;
                this.routeHighlightContent = null;
                this.routeHighlightStart = [0, 0];
                this.routeHighlightEnd = [0, 0];
                this.routeHighlighted.setStyle(this.geoJsonStyleNormal);
            },
            clickedOnMap(event) {
                if (event.originalEvent.target.nodeName !== 'path' && this.routeHighlightId != null) {
                    this.unfocusRouteHighlight();
                }
            },
            parseRoutes(response) {
                this.routes = response.data;
            },
            parseIncidents(response) {
                this.markers = response.data;
                for (var i = 0; i < this.markers.length; i++) {
                    this.incident_heatmap.push([this.markers[i].coordinates.coordinates[1], this.markers[i].coordinates.coordinates[0], 1]);
                }
            },
            geoJsonPopupOptions(marker) {
                return {
                    onEachFeature: function onEachFeature(feature, layer) {
                        layer.bindPopup(`<table><tr><td>RideId:</td><td>${marker.rideId}</td></tr><tr><td>Scary:</td><td>${marker.scary}</td></tr></table><p>${marker.description}</p>`);
                    }
                }
            }
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

            ApiService.loadRoutes(lat, lon).then(response => (this.parseRoutes(response)));
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
