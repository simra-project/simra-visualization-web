<template>
    <l-tile-layer url="http://207.180.205.80:1337/tiles/simra_junctions_stoptimes/{z}/{x}/{y}.png"/>
</template>

<script>
import {LTileLayer} from "vue2-leaflet";

export default {
    name: "StopTimesView",
    components: {
        LTileLayer,
    },
}
</script>

<style scoped>

</style>
