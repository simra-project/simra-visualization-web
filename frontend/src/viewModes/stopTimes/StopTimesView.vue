<template>
    <l-tile-layer :url="TILE_URL + '/tiles/stoptimes/{z}/{x}/{y}.png'"/>
</template>

<script>
import {LTileLayer} from "vue2-leaflet";

export default {
    name: "StopTimesView",
    components: {
        LTileLayer,
    },
    computed: {
        TILE_URL() {
            return process.env.VUE_APP_TILE_URL;
        },
    },
}
</script>

<style scoped>

</style>
