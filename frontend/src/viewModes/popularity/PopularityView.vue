<template>
    <div>
        <div class="leaflet-control topcenter rides-submode-switcher" v-if="subViewMode === config.subViewModes.POPULARITY_COMBINED || subViewMode === config.subViewModes.POPULARITY_AVOIDED || subViewMode === config.subViewModes.POPULARITY_CHOSEN || subViewMode === config.subViewModes.POPULARITY_SCORE">
            <b-tabs type="is-toggle-rounded"
                    :value="subViewMode"
                    @change="$emit('update:sub-view-mode', $event)"
                    @update="$emit('update:sub-view-mode', $event)"
            >
                <b-tab-item :label="$t('popularity.combined')" icon="chart-area"></b-tab-item>
                <b-tab-item :label="$t('popularity.avoided')" icon="database"></b-tab-item>
                <b-tab-item :label="$t('popularity.chosen')" icon="database"></b-tab-item>
                <b-tab-item :label="$t('popularity.score')" icon="database"></b-tab-item>
            </b-tabs>
        </div>

        <l-tile-layer
            v-if="subViewMode === config.subViewModes.POPULARITY_COMBINED"
            :url="TILE_URL + '/tiles/popularity-combined/{z}/{x}/{y}.png'"
        />
        <l-tile-layer
            v-if="subViewMode === config.subViewModes.POPULARITY_AVOIDED"
            :url="TILE_URL + '/tiles/popularity-original_avoided/{z}/{x}/{y}.png'"
        />
        <l-tile-layer
            v-if="subViewMode === config.subViewModes.POPULARITY_CHOSEN"
            :url="TILE_URL + '/tiles/popularity-original_chosen/{z}/{x}/{y}.png'"
        />
        <l-tile-layer
            v-if="subViewMode === config.subViewModes.POPULARITY_SCORE"
            :url="TILE_URL + '/tiles/popularity-score/{z}/{x}/{y}.png'"
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
    position: absolute;
    width: 500px;
    left: calc(50% - 250px);
    right: calc(50% - 250px);

    li > a {
        background-color: white;
        color: #3273dc;
    }
}
</style>
