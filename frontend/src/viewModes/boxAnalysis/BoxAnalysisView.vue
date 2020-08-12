<template>
    <div>
        <l-geo-json v-if="polygonResult" :geojson="polygonResult" :options="rideStyle" />
    </div>
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
    },
    data() {
        return {
            config: Config,
            polygonResult: [],
            polygonResultLoaded: false,
            polygonTool: null,
            rideStyle: {
                style: feature => {
                    return {
                        color: "#178a00",
                        weight: 1,
                        opacity: 0.8,
                    };
                },
            },
        }
    },
    methods: {
        initPolygonSelection(mapObject) {
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

                let coordinates = e.layer._latlngs[0].flatMap(x => [x.lng, x.lat])
                this.apiWorker.postMessage(["polygon", coordinates]);

                console.log("User selected polygon area: " + coordinates);
            });

            // Removing polygon and rides on click
            this.mapLayer.on('click', () => {
                this.polygonResult = [];
                this.mapLayer.clearLayers();
                this.polygonTool.enable();
            });
        },
        handleWorkerMessage(message) {
            switch (message.data[0]) {
                case "progress":
                    this.$emit('on-progress', message.data[1], message.data[2])
                    break;
                case "polygon":
                    this.polygonResult = {
                        "type": "FeatureCollection",
                        "features": message.data[1]
                    };
                    break;
            }
        },
    },
    async mounted() {
        this.apiWorker = new Worker("/ApiWorker.js");
        this.apiWorker.onmessage = this.handleWorkerMessage;
        this.apiWorker.postMessage(["backendUrl", ApiService.URL_BACKEND]);

        this.$nextTick(() => {
            this.initPolygonSelection(this.$parent.mapObject);
        });
    },
    beforeDestroy() {
        // Remove listeners and disable polygon tool
        this.$parent.mapObject.off(window.L.Draw.Event.CREATED);
        this.polygonTool.disable();

        this.mapLayer.off('click');
        this.mapLayer.clearLayers();
    }
};
</script>

<style lang="scss">
    @import '~leaflet-draw/dist/leaflet.draw.css';
</style>
