<template>
    <l-geo-json v-if="polygonResult" :geojson="polygonResult" :options="rideStyle" />
</template>

<script>
import { LGeoJson } from "vue2-leaflet";
import LDraw from "leaflet-draw";
import * as L from "leaflet";

import Config from "@/constants";
import { ApiService } from "@/services/ApiService";

export default {
    name: "BoxAnalysisView",
    components: {
        LGeoJson,
        LDraw,
    },
    props: {
        mapLayer: { default: {} },
        subViewMode: Number,
    },
    data() {
        return {
            config: Config,
            polygon: null,
            polygonResult: [],
            polygonResultLoaded: false,
            polygonTool: null,
            rideStyle: {
                style: feature => {
                    return {
                        color: ['#90ee90', '#6ccc6c', '#48a948', '#248724', '#006400'][(this.rideStyleHelperCounter++ % 5)],
                        weight: 2,
                        opacity: 0.8,
                    };
                },
            },
            rideStyleHelperCounter: 0,
        }
    },
    methods: {
        initPolygonSelection(mapObject) {
            // Fixing moving the map while adding polygon points
            // Source: https://gis.stackexchange.com/questions/341221/while-dragging-a-map-in-leaflet-it-marks-a-point-for-the-feature
            // 2nd Source: https://github.com/Leaflet/Leaflet.draw/issues/695
            const originalOnTouch = L.Draw.Polyline.prototype._onTouch;
            L.Draw.Polyline.prototype._onTouch = function (e) {
                if (e.originalEvent.pointerType !== "mouse" && e.originalEvent.pointerType !== "touch") {
                    return originalOnTouch.call(this, e);
                }
            };

            this.polygonTool = new L.Draw.Polygon(mapObject, {
                showArea: true,
                showLength: true,
            });
            this.polygonTool.enable();

            mapObject.on(window.L.Draw.Event.CREATED, (e) => {
                if (e.layerType !== 'polygon') return;

                this.polygonResult = [];
                this.mapLayer.clearLayers();
                this.mapLayer.addLayer(e.layer);

                this.polygon = e.layer._latlngs[0].flatMap(x => `${x.lng}+${x.lat}`);
                this.polygon.push(this.polygon[0]);
                this.loadRides();

                console.log("User selected polygon area: " + this.polygon);
            });

            // Removing polygon and rides on click
            this.mapLayer.on('click', () => {
                this.polygon = null;
                this.polygonResult = [];
                this.mapLayer.clearLayers();
                this.polygonTool.enable();
            });
        },
        loadRides() {
            let mode = ["containsStart", "contains", "containsEnd"][this.subViewMode];
            this.apiWorker.postMessage(["polygon", this.polygon, mode]);
        },
        handleWorkerMessage(message) {
            switch (message.data[0]) {
                case "progress":
                    this.$emit('on-progress', message.data[1], message.data[2])
                    break;
                case "polygon":
                    this.polygonResult = message.data[1];
                    break;
            }
        },
        initLeafletDrawTranslations() {
            L.drawLocal.draw.handlers.polygon.tooltip.start = this.$t('boxAnalysis.draw.polygonStart');
            L.drawLocal.draw.handlers.polygon.tooltip.cont = this.$t('boxAnalysis.draw.polygonContinue');
            L.drawLocal.draw.handlers.polygon.tooltip.end = this.$t('boxAnalysis.draw.polygonEnd');
        },
    },
    async mounted() {
        this.apiWorker = new Worker("/ApiWorker.js");
        this.apiWorker.onmessage = this.handleWorkerMessage;
        this.apiWorker.postMessage(["backendUrl", ApiService.URL_BACKEND]);
        this.initLeafletDrawTranslations();

        this.$nextTick(() => {
            // Awkwardly waiting for the map layer object to be created
            setTimeout(() => {
                this.initPolygonSelection(this.$parent.mapObject);
            }, 500);
        });
    },
    beforeDestroy() {
        // Remove listeners and disable polygon tool
        this.$parent.mapObject.off(window.L.Draw.Event.CREATED);
        this.polygonTool.disable();

        this.mapLayer.off('click');
        this.mapLayer.clearLayers();
    },
    watch: {
        subViewMode: function (newValue, oldValue) {
            if (newValue !== oldValue && this.polygon !== null) this.loadRides();
        },
        $i18n: function (newValue, oldValue) {
            if (newValue !== oldValue) this.initLeafletDrawTranslations();
        }
    }
};
</script>

<style lang="scss">
    @import '~leaflet-draw/dist/leaflet.draw.css';
</style>
