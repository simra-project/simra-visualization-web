<template>
    <div>
        <div class="leaflet-control topcenter surface-quality-submode-switcher">
            <b-tabs type="is-toggle-rounded"
                    :value="subViewMode"
                    @change="$emit('update:sub-view-mode', $event)"
                    @update="$emit('update:sub-view-mode', $event)"
            >
                <b-tab-item :label="$t('surfaceQuality.aggregated')" icon="chart-area"></b-tab-item>
                <b-tab-item :label="$t('surfaceQuality.singleRides')" icon="database"></b-tab-item>
            </b-tabs>
        </div>

        <l-tile-layer v-if="subViewMode === config.subViewModes.SURFACE_QUALITY_AGGREGATED" :url="TILE_URL + '/tiles/surface-quality-aggregated/{z}/{x}/{y}.png'"/>
        <l-tile-layer v-if="subViewMode === config.subViewModes.SURFACE_QUALITY_SINGLE" :url="TILE_URL + '/tiles/surface-quality-single/{z}/{x}/{y}.png'"/>
    </div>
</template>

<script>
import {LTileLayer} from "vue2-leaflet";
import Config from "@/constants";

export default {
    name: "SurfaceQualityView",
    components: {
        LTileLayer,
    },
    props: {
        subViewMode: Number,
    },
    data() {
        return {
            config: Config
        }
    },
    computed: {
        TILE_URL() {
            return process.env.VUE_APP_TILE_URL;
        },
    },
}
</script>

<style lang="scss">
    .surface-quality-submode-switcher {
        width: 336px !important;
        left: calc(50% - 168px) !important;
        right: calc(50% - 168px) !important;

        .b-tabs {
            margin-top: 16px;

            li > a {
                background-color: white;
                color: #3273dc;
            }
        }
    }
</style>
