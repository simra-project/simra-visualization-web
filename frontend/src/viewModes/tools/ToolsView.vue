<template>
    <div>
        <!-- drag and drop implementation basierend auf: https://codepen.io/nguernse/pen/JyYdNY-->
        <div style="visibility:hidden; opacity:0" id="dropzone">
            <div id="textnode">Fahrt aus CSV anzeigen</div>
        </div>

        <l-geo-json :geojson="importedRide" :options="rideStyle" v-if="importedRide !== null"  /><!--@ready="tofront"-->
        <l-geo-json :geojson="importedIncidents" :options="markerStyle" v-if="importedIncidents !== null" />
    </div>
</template>

<script>
import { LGeoJson } from "vue2-leaflet";
import * as L from "leaflet";
import { ExtraMarkers } from "leaflet-extra-markers";
import Papa from "papaparse";

import Config from "@/constants";

export default {
    name: "ToolsView",
    components: {
        LGeoJson,
    },
    props: {
    },
    data() {
        return {
            config: Config,
            importedRide: null,
            importedIncidents: null,

            // Styles
            rideStyle: {
                color: "hsl(354,100%,43%)",
                weight: 4,
            },
            markerStyle: {
                pointToLayer: (feature, latlng) => L.marker(latlng, {
                    icon: ExtraMarkers.icon({
                        icon: "fa-file-import",
                        markerColor: "orange-dark",
                        prefix: "fa",
                    }),
                }),
            },
        }
    },

    methods: {
        readFile(file) {
            let self = this;
            let reader = new FileReader();
            reader.onload = function (e) {
                let content = e.target.result;
                let seperator = /\n[=]+\n.*\n/mg;
                let newline = /#.*\n/mg;
                let split = content.split(seperator, 2);
                let incidents = split[0].split(newline, 2)[1];
                let route = split[1];
                let data = Papa.parse(route, {header: true, skipEmptyLines: true});
                let geoJsonRide = {
                    type: "LineString",
                    coordinates: []
                };
                for (let line of data.data) {
                    if (line.lat !== "" && line.lon !== "") {
                        geoJsonRide.coordinates.push([Number(line.lon), Number(line.lat)]);
                    }
                }
                self.importedRide = geoJsonRide;
                self.$emit('fit-in-view', L.geoJSON(geoJsonRide));

                incidents = Papa.parse(incidents, {header: true, skipEmptyLines: true});
                let geoJsonIncidents = {
                    type: "MultiPoint",
                    coordinates: []
                };
                for (let incident of incidents.data) {
                    geoJsonIncidents.coordinates.push([Number(incident.lon), Number(incident.lat)])
                }
                self.importedIncidents = geoJsonIncidents;
            };
            reader.readAsText(file, "UTF-8");
        },
    },
    async mounted() {
        window.addEventListener("dragenter", function (e) {
            document.querySelector("#dropzone").style.visibility = "";
            document.querySelector("#dropzone").style.opacity = 1;
            document.querySelector("#textnode").style.fontSize = "48px";
        });

        window.addEventListener("dragleave", function (e) {
            e.preventDefault();
            document.querySelector("#dropzone").style.visibility = "hidden";
            document.querySelector("#dropzone").style.opacity = 0;
            document.querySelector("#textnode").style.fontSize = "42px";

        });

        window.addEventListener("dragover", function (e) {
            e.preventDefault();
            document.querySelector("#dropzone").style.visibility = "";
            document.querySelector("#dropzone").style.opacity = 1;
            document.querySelector("#textnode").style.fontSize = "48px";
        });

        let self = this;
        window.addEventListener("drop", function (e) {
            e.preventDefault();
            document.querySelector("#dropzone").style.visibility = "hidden";
            document.querySelector("#dropzone").style.opacity = 0;
            document.querySelector("#textnode").style.fontSize = "42px";

            const files = e.dataTransfer.files;
            console.log("Drop files:", files);
            if (!files) return;

            // this tip, convert FileList to array, credit: https://www.smashingmagazine.com/2018/01/drag-drop-file-uploader-vanilla-js/
            self.readFile(Array.from(files).pop());
        });
    },
};
</script>

<style lang="less">
    div#dropzone {
        position: fixed;
        top: 0;
        left: 0;
        z-index: 9999999999;
        width: 100%;
        height: 100%;
        transition: visibility 175ms, opacity 175ms;
        display: table;
        text-shadow: 1px 1px 2px #000;
        color: #fff;
        background: rgba(0, 0, 0, 0.45);
        font: bold 42px Oswald, DejaVu Sans, Tahoma, sans-serif;
    }

    div#textnode {
        display: table-cell;
        text-align: center;
        vertical-align: middle;
        transition: font-size 175ms;
    }
</style>
