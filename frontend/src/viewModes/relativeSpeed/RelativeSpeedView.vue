<template>
    <l-tile-layer url="http://207.180.205.80:1337/tiles/relative-speed/{z}/{x}/{y}.png"/>
</template>

<script>
import {LTileLayer} from "vue2-leaflet";

export default {
    name: "RelativeSpeedView",
    components: {
        LTileLayer,
    },
}
</script>

<style scoped>

</style>
