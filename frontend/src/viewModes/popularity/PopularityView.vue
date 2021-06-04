<template>
    <div>
        <l-tile-layer
            v-if="subViewMode === config.subViewModes.POPULARITY_COMBINED"
            :url="TILE_URL + '/tiles/popularity-combined/{z}/{x}/{y}.png'"
        />
        <l-tile-layer
            v-if="subViewMode === config.subViewModes.POPULARITY_AVOIDED"
            :url="TILE_URL + '/tiles/popularity-avoided/{z}/{x}/{y}.png'"
        />
        <l-tile-layer
            v-if="subViewMode === config.subViewModes.POPULARITY_CHOSEN"
            :url="TILE_URL + '/tiles/popularity-chosen/{z}/{x}/{y}.png'"
        />
    </div>
</template>

<script>
import { LGeoJson, LTileLayer } from "vue2-leaflet";

import Config from "@/constants";

export default {
    name: "PopularityView",
    components: {
        LGeoJson,
        LTileLayer
    },
    props: {
        subViewMode: Number
    },
    data() {
        return {
            config: Config
        };
    },
    computed: {
        TILE_URL() {
            return process.env.VUE_APP_TILE_URL;
        }
    }
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
