<template>
    <div>
        <div class="leaflet-control topcenter rides-submode-switcher" v-if="subViewMode === config.subViewModes.RIDES_DENSITY_ALL || subViewMode === config.subViewModes.RIDES_ORIGINAL">
            <b-tabs type="is-toggle-rounded"
                    :value="subViewMode"
                    @change="$emit('update:sub-view-mode', $event)"
                    @update="$emit('update:sub-view-mode', $event)"
            >
                <b-tab-item :label="$t('ride.density')" icon="chart-area"></b-tab-item>
                <b-tab-item :label="$t('ride.originalRides')" icon="database"></b-tab-item>
            </b-tabs>
        </div>

        <l-tile-layer v-if="subViewMode === config.subViewModes.RIDES_DENSITY_ALL" :url="TILE_URL + '/tiles/rides-density_all/{z}/{x}/{y}.png'"/>
        <l-tile-layer v-if="subViewMode === config.subViewModes.RIDES_DENSITY_RUSHHOURMORNING" :url="TILE_URL + '/tiles/rides-density_rushhourmorning/{z}/{x}/{y}.png'"/>
        <l-tile-layer v-if="subViewMode === config.subViewModes.RIDES_DENSITY_RUSHHOUREVENING" :url="TILE_URL + '/tiles/rides-density_rushhourevening/{z}/{x}/{y}.png'"/>
        <l-tile-layer v-if="subViewMode === config.subViewModes.RIDES_DENSITY_WEEKEND" :url="TILE_URL + '/tiles/rides-density_weekend/{z}/{x}/{y}.png'"/>
        <l-tile-layer v-if="subViewMode === config.subViewModes.RIDES_ORIGINAL" :url="TILE_URL + '/tiles/rides-original/{z}/{x}/{y}.png'"/>
    </div>
</template>

<script>
import { LGeoJson, LTileLayer } from "vue2-leaflet";

import Config from "@/constants";

export default {
    name: "RideView",
    components: {
        LGeoJson,
        LTileLayer,
    },
    props: {
        subViewMode: Number,
    },
    data() {
        return {
            config: Config,
        }
    },
    computed: {
        TILE_URL() {
            return process.env.VUE_APP_TILE_URL;
        },
    },
};
</script>

<style lang="scss">
    .rides-submode-switcher .b-tabs {
        margin-top: 16px;

        li > a {
            background-color: white;
            color: #3273dc;
        }
    }
</style>
