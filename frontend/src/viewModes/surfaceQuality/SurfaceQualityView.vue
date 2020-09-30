<template>
    <l-tile-layer url="http://207.180.205.80:1337/tiles/simra_surface_quality/{z}/{x}/{y}.png"/>
</template>

<script>
import {LTileLayer} from "vue2-leaflet";

export default {
    name: "SurfaceQualityView",
    components: {
        LTileLayer,
    },
}
</script>

<style scoped>

</style>
