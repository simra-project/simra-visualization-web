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
                    <b-switch v-model="showAccidents">Show Accidents</b-switch>
                </div>
            </div>
        </l-control>
    </l-map>
</template>

<script>
import { LMap, LTileLayer, LControl } from "vue2-leaflet";
import { ToastProgrammatic as Toast } from "buefy";

export default {
    components: {
        LMap,
        LTileLayer,
        LControl,
    },
    data () {
        return {
            url: "http://{s}.tile.osm.org/{z}/{x}/{y}.png",
            zoom: 15,
            center: [52.5125322, 13.3269446],
            bounds: null,
            showTrips: true,
            showAccidents: true,
        };
    },
    methods: {
        zoomUpdated (zoom) {
            this.zoom = zoom;
        },
        centerUpdated (center) {
            this.center = center;
        },
        boundsUpdated (bounds) {
            this.bounds = bounds;
        },
        showFakeLoading () {
            let message = "";
            if (this.showTrips) message = "Loading trips ...";
            if (this.showAccidents) message = "Loading accidents ...";
            if (this.showTrips && this.showAccidents) message = "Loading trips and accidents ...";

            if (this.showTrips || this.showAccidents) {
                Toast.open({
                    message: message,
                    position: "is-bottom",
                });
            }
        },
    },
    watch: {
        showTrips (newVal, oldVal) {
            this.showFakeLoading();
        },
        showAccidents (newVal, oldVal) {
            this.showFakeLoading();
        },
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
    }
</style>
