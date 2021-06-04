<template>
    <div>
        <Navigation
            :map-style="mapStyle"
            :center="center"
            :zoom="zoom"
            @showAboutModal="showAboutModal = true"
            @update:map-style="
                mapStyle = $event;
                updateUrlQuery();
            "
        />

        <div class="main-layout" style="display:flex;">
            <Sidebar
                v-model.sync="viewMode"
                ref="sidebar"
                :sub-view-mode="subViewMode"
                @size-changed="mapObject.invalidateSize()"
                @update:sub-view-mode="subViewMode = $event"
            />

            <section class="hero is-fullheight-with-navbar viewmode-container">
                <l-map
                    ref="map"
                    style="width: 100%"
                    :zoom="zoom"
                    :center="center"
                    :min-zoom="2"
                    @update:zoom="zoom = $event"
                    @update:center="
                        center = $event;
                        updateUrlQuery();
                    "
                    @update:bounds="bounds = $event"
                    @click="clickedOnMap"
                    :style="{
                        'display: none':
                            viewMode === config.viewModes.STATISTICS
                    }"
                >
                    <l-tile-layer
                        :url="mapStyle.url"
                        :class="{ monochrome: false }"
                        :attribution="mapStyle.attribution"
                    />

                    <div class="leaflet-control bottomcenter">
                        <div
                            class="loading-container"
                            v-if="loadingProgress !== null"
                            :class="{ invisible: loadingProgress === 100 }"
                        >
                            <div class="overlay overlay-loading">
                                <div class="spinner-container">
                                    <scaling-squares-spinner
                                        :animation-duration="1750"
                                        :size="30"
                                        color="hsl(217, 71%, 53%)"
                                    />

                                    <div class="text">
                                        {{ $t("map.loadingMapData") }}
                                    </div>
                                </div>

                                <b-progress
                                    type="is-primary is-small"
                                    :value="loadingProgress"
                                />
                            </div>
                        </div>
                    </div>

                    <l-control
                        position="bottomright"
                        v-if="config.viewModeHasLegend(viewMode)"
                    >
                        <MapLegend
                            :view-mode="viewMode"
                            :sub-view-mode="subViewMode"
                            :zoom="zoom"
                            class="is-hidden-mobile"
                        />
                    </l-control>

                    <!-- Because of a Vue/DOM problem, view modes have to be declared this way ... (wrapped in a tag) -->
                    <div>
                        <RideView
                            v-if="viewMode === config.viewModes.RIDES"
                            ref="rideView"
                            :sub-view-mode="subViewMode"
                            @on-progress="updateLoadingView"
                            @update:sub-view-mode="subViewMode = $event"
                        />
                    </div>
                    <div>
                        <IncidentView
                            v-if="viewMode === config.viewModes.INCIDENTS"
                            ref="incidentView"
                            :zoom="zoom"
                            :bounds="bounds"
                            @on-progress="updateLoadingView"
                            @fit-in-view="fitMapObjectIntoView"
                        />
                    </div>
                    <div>
                        <SurfaceQualityView
                            v-if="viewMode === config.viewModes.SURFACE_QUALITY"
                            ref="surfaceQualityView"
                            :sub-view-mode="subViewMode"
                            @on-progress="updateLoadingView"
                            @update:sub-view-mode="subViewMode = $event"
                        />
                    </div>
                    <div>
                        <RelativeSpeedView
                            v-if="viewMode === config.viewModes.RELATIVE_SPEED"
                            ref="relativeSpeedView"
                            :sub-view-mode="subViewMode"
                            @on-progress="updateLoadingView"
                            @update:sub-view-mode="subViewMode = $event"
                        />
                    </div>
                    <div>
                        <StopTimesView
                            v-if="viewMode === config.viewModes.STOP_TIMES"
                            ref="stopTimesView"
                            @on-progress="updateLoadingView"
                        />
                    </div>
                    <div>
                        <BoxAnalysisView
                            v-if="viewMode === config.viewModes.BOX_ANALYSIS"
                            ref="boxAnalysisView"
                            :mapLayer="boxAnalysisMapLayer"
                            :sub-view-mode="subViewMode"
                            @on-progress="updateLoadingView"
                            @update:sub-view-mode="subViewMode = $event"
                        />
                    </div>
                    <div>
                        <ToolsView
                            v-if="viewMode === config.viewModes.TOOLS"
                            ref="toolsView"
                            @fit-in-view="fitMapObjectIntoView"
                        />
                    </div>
                    <div>
                        <PopularityView
                            v-if="viewMode === config.viewModes.POPULARITY"
                            ref="popularityView"
                            :sub-view-mode="subViewMode"
                            @on-progress="updateLoadingView"
                            @update:sub-view-mode="subViewMode = $event"
                        />
                    </div>

                    <l-tile-layer
                        v-if="mapStyle.hasLabelLayer"
                        :url="mapStyle.urlLabels"
                        pane="overlayPane"
                    />
                </l-map>

                <div
                    class="statistics-container"
                    v-if="viewMode === config.viewModes.STATISTICS"
                >
                    <Statistics :regions="regions" ref="statisticsView" />
                </div>
            </section>
        </div>

        <b-modal
            :active="showAboutModal"
            @close="showAboutModal = false"
            style="z-index: 999"
        >
            <About />
        </b-modal>
    </div>
</template>

<script>
import { LControl, LMap, LTileLayer } from "vue2-leaflet";
import { ScalingSquaresSpinner } from "epic-spinners";

import Config from "@/constants";
import MapLegend from "@/components/MapLegend";
import Navigation from "@/components/Navigation";
import Sidebar from "@/components/Sidebar";
import About from "@/views/About";

import RideView from "@/viewModes/ride/RideView";
import IncidentView from "@/viewModes/incident/IncidentView";
import SurfaceQualityView from "@/viewModes/surfaceQuality/SurfaceQualityView";
import RelativeSpeedView from "@/viewModes/relativeSpeed/RelativeSpeedView";
import StopTimesView from "@/viewModes/stopTimes/StopTimesView";
import BoxAnalysisView from "@/viewModes/boxAnalysis/BoxAnalysisView";
import ToolsView from "@/viewModes/tools/ToolsView";
import PopularityView from "@/viewModes/popularity/PopularityView";
import Statistics from "@/views/Statistics";

export default {
    components: {
        // Map
        LMap,
        LTileLayer,
        LControl,
        ScalingSquaresSpinner,
        // Components
        MapLegend,
        Navigation,
        Sidebar,
        // View Modes
        RideView,
        IncidentView,
        SurfaceQualityView,
        RelativeSpeedView,
        StopTimesView,
        BoxAnalysisView,
        ToolsView,
        PopularityView,
        Statistics,
        About
    },
    data() {
        return {
            // General
            config: Config,
            viewMode: parseInt(this.$route.query.m) || Config.viewModes.RIDES,
            subViewMode:
                parseInt(this.$route.query.sm) || Config.subViewModes.DEFAULT,
            loadingProgress: null,
            regions: null,
            showAboutModal: false,

            // Map
            mapObject: null,
            mapStyle:
                Config.mapStyles[this.$route.query.style] ||
                Config.getDefaultMapStyle(),
            zoom: parseInt(this.$route.query.z) || 15,
            center: [
                this.$route.query.lat || 52.5125322,
                this.$route.query.lng || 13.3269446
            ],
            bounds: null,

            // Extras for view modes
            boxAnalysisMapLayer: null
        };
    },
    methods: {
        updateUrlQuery() {
            this.$router
                .replace({
                    name: "mapQuery",
                    params: {
                        lat: this.center.lat,
                        lng: this.center.lng,
                        zoom: this.zoom,
                        style: this.mapStyle.key,
                        viewMode: this.viewMode,
                        subViewMode: this.subViewMode
                    }
                })
                .catch(() => {});
        },
        fitMapObjectIntoView(mapObject) {
            // Fitting a map object (e.g. a ride) into view if it's not already
            let bounds = mapObject.getBounds().pad(0.1);
            if (!this.bounds.contains(bounds)) {
                this.mapObject.flyToBounds(bounds);
            }
        },
        clickedOnMap(event) {
            if (this.viewMode === Config.viewModes.INCIDENTS) {
                this.$refs.incidentView.clickedOnMap(event);
            }
        },
        updateLoadingView(progress, expectedTotal) {
            if (expectedTotal === 0) return;
            let currentProgress = Math.min(
                Math.max(progress / expectedTotal, 0.0),
                1.0
            );

            const minOffset = 0.1;
            this.loadingProgress =
                Math.min(minOffset + currentProgress * (1.0 - minOffset), 1.0) *
                100;

            if (progress === expectedTotal) {
                setTimeout(() => (this.loadingProgress = null), 1200);
            }
        }
    },
    async mounted() {
        fetch("http://207.180.205.80:8000/api/regions/")
            .then(r => r.json())
            .then(r => (this.regions = r));

        this.$nextTick(() => {
            this.mapObject = this.$refs.map.mapObject;
            this.zoom = this.mapObject.getZoom();
            this.center = this.mapObject.getCenter();
            this.bounds = this.mapObject.getBounds();
            this.updateUrlQuery();

            this.boxAnalysisMapLayer = new window.L.FeatureGroup().addTo(
                this.mapObject
            );
        });
    },
    watch: {
        viewMode: function(newValue, oldValue) {
            this.updateUrlQuery();
            this.updateLoadingView(1, 1);
        },
        subViewMode: function(newValue, oldValue) {
            this.updateUrlQuery();
        }
    }
};
</script>

<style lang="scss">
$sidebar-width: 304px;

.viewmode-container {
    border-top: none;
    min-height: calc(100vh - 57px) !important;
    width: calc(100vw - #{$sidebar-width} - 1px);
}

.vue2leaflet-map {
    height: 100%;
    width: 100%;
    flex: 1 0;

    // The class monochrome can only be set on the element before
    .monochrome
        ~ .leaflet-pane.leaflet-map-pane
        .leaflet-pane.leaflet-tile-pane
        .leaflet-layer:nth-child(1) {
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
                filter: grayscale(0.9);

                .scaling-squares-spinner,
                .scaling-squares-spinner .square {
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
        -webkit-box-shadow: 0 2px 3px rgba(10, 10, 10, 0.1),
            0 0 0 1px rgba(10, 10, 10, 0.1);
        box-shadow: 0 2px 3px rgba(10, 10, 10, 0.1),
            0 0 0 1px rgba(10, 10, 10, 0.1);
        color: #4a4a4a;
        position: relative;
    }

    &.leaflet-control-zoom {
        display: none;
    }
}

.statistics-container {
    position: fixed;
    max-height: 100%;
    width: calc(100vw - #{$sidebar-width} - 1px);
    overflow-y: scroll;
    z-index: 1000;
    background: #f3f3f3;
}

.sidebar.small + .viewmode-container {
    &,
    .statistics-container {
        width: calc(100vw - 72px);
    }
}
</style>
