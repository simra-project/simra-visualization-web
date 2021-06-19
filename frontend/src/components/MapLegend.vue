<template>
    <div class="overlay legend" > <!-- :class="{viewModeIncidents: viewMode === config.viewModes.INCIDENTS}" -->
        <template v-if="viewMode === config.viewModes.RIDES && subViewMode !== config.subViewModes.RIDES_ORIGINAL">
            <div class="symbol-container">
                <div class="symbol symbol-box blue1 one-third"/>
                <div class="symbol symbol-box blue2 two-thirds"/>
                <div class="symbol symbol-box blue3"/>
            </div>
            <div class="text-box">{{ $t('legend.ridesBicyclists') }}</div>
        </template>

        <template v-if="viewMode === config.viewModes.RIDES && subViewMode === config.subViewModes.RIDES_ORIGINAL">
                <div class="symbol symbol-line" style="background-color: #3279dc"/>
                <div class="text-box">{{ $t('legend.ridesSingle') }}</div>

                <div class="symbol symbol-circle" style="background-color: #9e1a16"/>
                <div class="text-box">{{ $t('legend.scaryIncident') }}</div>

                <div class="symbol symbol-circle" style="background-color: #32c0dc"/>
                <div class="text-box">{{ $t('legend.regularIncident') }}</div>
        </template>

        <!-- Incidents combined -->
        <template v-if="viewMode === config.viewModes.INCIDENTS">
            <template v-if="zoom > 15">
                <div class="symbol symbol-marker marker-scary"><i class="fa fa-car"/></div>
                <div class="text-box">{{ $t('legend.scaryIncident') }}</div>

                <div class="symbol symbol-marker marker-regular"><i class="fa fa-car"/></div>
                <div class="text-box">{{ $t('legend.regularIncident') }}</div>
            </template>
            <template v-else>
                <div class="symbol-container">
                    <div class="symbol symbol-circle" style="background-color: #9e1a16"/>
                    <div class="symbol symbol-circle" style="background-color: #777777"/>
                </div>
                <div class="text-box">{{ $t('legend.incidents') }}</div>
            </template>

            <div class="symbol-container">
                <div class="symbol symbol-box gradient1"/>
                <div class="symbol symbol-box gradient2" v-if="zoom <= 15"/>
                <div class="symbol symbol-box gradient3"/>
                <div class="symbol symbol-box gradient4"/>
                <div class="symbol symbol-box gradient5"/>
                <div class="symbol symbol-box gradient6" v-if="zoom <= 15"/>
            </div>
            <div class="text-box">{{ $t('legend.incidentGradient') }}</div>
        </template>

        <template v-if="viewMode === config.viewModes.SURFACE_QUALITY">
            <div class="symbol-container">
                <div class="symbol symbol-box gradient1"/>
                <div class="symbol symbol-box gradient2"/>
                <div class="symbol symbol-box gradient3"/>
                <div class="symbol symbol-box gradient4"/>
                <div class="symbol symbol-box gradient5"/>
                <div class="symbol symbol-box gradient6"/>
            </div>
            <div class="text-box">{{ subViewMode === config.subViewModes.SURFACE_QUALITY_AGGREGATED ? $t('legend.surfaceQualityGradientAggregated') : $t('legend.surfaceQualityGradientSingle') }}</div>
        </template>

        <template v-if="viewMode === config.viewModes.RELATIVE_SPEED">
            <div class="symbol symbol-box gradient1" style="width: 53px"/>
            <div class="text-box">{{ $t('legend.relativeSpeedFaster') }}</div>

            <div class="symbol symbol-box gradient2" style="width: 53px"/>
            <div class="text-box">{{ $t('legend.relativeSpeedAverage') }}</div>

            <div class="symbol-container">
                <div class="symbol symbol-box gradient3"/>
                <div class="symbol symbol-box gradient4"/>
                <div class="symbol symbol-box gradient5"/>
            </div>
            <div class="text-box">{{ $t('legend.relativeSpeedSlower') }}</div>
        </template>

        <template v-if="viewMode === config.viewModes.STOP_TIMES">
            <div class="symbol-container">
                <div class="symbol symbol-circle gradient1"/>
                <div class="symbol symbol-circle gradient2"/>
                <div class="symbol symbol-circle gradient3"/>
                <div class="symbol symbol-circle gradient4"/>
                <div class="symbol symbol-circle gradient5"/>
                <div class="symbol symbol-circle gradient6"/>
            </div>
            <div class="text-box">{{ $t('legend.stopTimes') }}</div>
        </template>

        <!-- Popularity score p_score -->
        <template v-if="viewMode === config.viewModes.POPULARITY && (subViewMode === config.subViewModes.POPULARITY_COMBINED || subViewMode === config.subViewModes.POPULARITY_W_INCIDENTS_COMBINED)">
            <!--
            Show different incident markes, depending on zoom level.
                16 - 18 Drop formed markers with icons.
                14 - 15 Grey and red dots.
                   < 14 None.
            -->
            <template v-if="zoom >= 16 && incidentsVisible">
                <div class="symbol symbol-marker marker-scary"><i class="fa fa-car"/></div>
                <div class="text-box">{{ $t('legend.scaryIncident') }}</div>

                <div class="symbol symbol-marker marker-regular"><i class="fa fa-car"/></div>
                <div class="text-box">{{ $t('legend.regularIncident') }}</div>
            </template>
            <template v-else-if="zoom >= 14 && incidentsVisible">
                <div class="symbol-container">
                    <div class="symbol symbol-circle" style="background-color: #9e1a16"/>
                    <div class="symbol symbol-circle" style="background-color: #777777"/>
                </div>
                <div class="text-box">{{ $t('legend.incidents') }}</div>
            </template>

            <div class="symbol-container">
                <div class="symbol symbol-box gradient6"/>
                <div class="symbol symbol-box gradient5"/>
                <div class="symbol symbol-box gradient4"/>
                <div class="symbol symbol-box gradient3"/>
                <div class="symbol symbol-box gradient2"/>
                <div class="symbol symbol-box gradient1"/>
            </div>
            <div class="text-box">{{ $t('legend.popularityCombined') }}</div>
        </template>

        <!-- Avoided street segment density -->
        <template v-if="viewMode === config.viewModes.POPULARITY && (subViewMode == config.subViewModes.POPULARITY_AVOIDED || subViewMode == config.subViewModes.POPULARITY_W_INCIDENTS_AVOIDED)">
            <!--
            Show different incident markes, depending on zoom level.
                16 - 18 Drop formed markers with icons.
                14 - 15 Grey and red dots.
                   < 14 None.
            -->
            <template v-if="zoom >= 16 && incidentsVisible">
                <div class="symbol symbol-marker marker-scary"><i class="fa fa-car"/></div>
                <div class="text-box">{{ $t('legend.scaryIncident') }}</div>

                <div class="symbol symbol-marker marker-regular"><i class="fa fa-car"/></div>
                <div class="text-box">{{ $t('legend.regularIncident') }}</div>
            </template>
            <template v-else-if="zoom >= 14 && incidentsVisible">
                <div class="symbol-container">
                    <div class="symbol symbol-circle" style="background-color: #9e1a16"/>
                    <div class="symbol symbol-circle" style="background-color: #777777"/>
                </div>
                <div class="text-box">{{ $t('legend.incidents') }}</div>
            </template>

            <div class="symbol-container">
                <div class="symbol symbol-box blue1 one-third"/>
                <div class="symbol symbol-box blue2 two-thirds"/>
                <div class="symbol symbol-box blue3"/>
            </div>
            <div class="text-box">{{ $t('legend.avoidedStreetSegments') }}</div>
        </template>

        <!-- Chosen street segment density -->
        <template v-if="viewMode === config.viewModes.POPULARITY && (subViewMode == config.subViewModes.POPULARITY_CHOSEN || subViewMode == config.subViewModes.POPULARITY_W_INCIDENTS_CHOSEN)">
            <!--
            Show different incident markes, depending on zoom level.
                16 - 18 Drop formed markers with icons.
                14 - 15 Grey and red dots.
                   < 14 None.
            -->
            <template v-if="zoom >= 16 && incidentsVisible">
                <div class="symbol symbol-marker marker-scary"><i class="fa fa-car"/></div>
                <div class="text-box">{{ $t('legend.scaryIncident') }}</div>

                <div class="symbol symbol-marker marker-regular"><i class="fa fa-car"/></div>
                <div class="text-box">{{ $t('legend.regularIncident') }}</div>
            </template>
            <template v-else-if="zoom >= 14 && incidentsVisible">
                <div class="symbol-container">
                    <div class="symbol symbol-circle" style="background-color: #9e1a16"/>
                    <div class="symbol symbol-circle" style="background-color: #777777"/>
                </div>
                <div class="text-box">{{ $t('legend.incidents') }}</div>
            </template>

            <div class="symbol-container">
                <div class="symbol symbol-box blue1 one-third"/>
                <div class="symbol symbol-box blue2 two-thirds"/>
                <div class="symbol symbol-box blue3"/>
            </div>
            <div class="text-box">{{ $t('legend.chosenStreetSegments') }}</div>
        </template>

        <!-- Mixed popularity score p_mscore -->
        <template v-if="viewMode === config.viewModes.POPULARITY && (subViewMode === config.subViewModes.POPULARITY_SCORE || subViewMode === config.subViewModes.POPULARITY_W_INCIDENTS_SCORE)">
            <!--
            Show different incident markes, depending on zoom level.
                16 - 18 Drop formed markers with icons.
                14 - 15 Grey and red dots.
                   < 14 None.
            -->
            <template v-if="zoom >= 16 && incidentsVisible">
                <div class="symbol symbol-marker marker-scary"><i class="fa fa-car"/></div>
                <div class="text-box">{{ $t('legend.scaryIncident') }}</div>

                <div class="symbol symbol-marker marker-regular"><i class="fa fa-car"/></div>
                <div class="text-box">{{ $t('legend.regularIncident') }}</div>
            </template>
            <template v-else-if="zoom >= 14 && incidentsVisible">
                <div class="symbol-container">
                    <div class="symbol symbol-circle" style="background-color: #9e1a16"/>
                    <div class="symbol symbol-circle" style="background-color: #777777"/>
                </div>
                <div class="text-box">{{ $t('legend.incidents') }}</div>
            </template>

            <div class="symbol-container">
                <div class="symbol symbol-box gradient6"/>
                <div class="symbol symbol-box gradient5"/>
                <div class="symbol symbol-box gradient4"/>
                <div class="symbol symbol-box gradient3"/>
                <div class="symbol symbol-box gradient2"/>
                <div class="symbol symbol-box gradient1"/>
            </div>
            <div class="text-box">{{ $t('legend.popularityCombined') }}</div>
        </template>
    </div>
</template>

<script>
import Config from "@/constants";

export default {
    name: "MapLegend",
    props: {
        viewMode: Number,
        subViewMode: Number,
        zoom: Number,
        incidentsVisible: Boolean,
    },
    data() {
        return {
            config: Config,
        }
    },
    watch: {
        incidentsVisible() {
            return this.incidentsVisible;
        }
    }
};
</script>

<style lang="scss">
    .legend {
        display: grid;
        grid-template-columns: auto auto;
        grid-column-gap: 8px;
        grid-row-gap: 4px;
        padding: 6px 8px;
        border: 1px solid #b5b5b5cc;
        -webkit-box-shadow: none;
        box-shadow: none;
        background-color: white;
        border-radius: 4px;

        & > * {
            align-self: center;

            &:nth-child(odd) {
                justify-self: end;
            }

            &.text-box {
                font-size: 14px;
            }
        }
    }

    .symbol {
        width: 15px;
        height: 15px;

        & + .symbol {
            margin-left: 4px;
        }

        &.symbol-box {
            &.one-third {
                height: 7px;
            }

            &.two-thirds {
                height: 11px;
            }
        }

        &.symbol-circle {
            border-radius: 15px;
        }

        &.symbol-line {
            height: 3px;
            margin: 6px 0;
        }

        &.blue1 { background-color: hsl(190, 71%, 53%); opacity: 0.8; }
        &.blue2 { background-color: hsl(215, 71%, 53%); opacity: 0.9; }
        &.blue3 { background-color: hsl(240, 71%, 53%); }

        &.red1 {background-color: hsl(80, 80%, 48%); }
        &.red2 {background-color: hsl(55, 80%, 48%); }
        &.red3 {background-color: hsl(30, 80%, 48%); }

        &.gradient1 { background-color: rgb(68, 153, 53); }
        &.gradient2 { background-color: rgb(142, 192, 84); }
        &.gradient3 { background-color: rgb(237, 226, 4); }
        &.gradient4 { background-color: rgb(239, 125, 7); }
        &.gradient5 { background-color: rgb(227, 11, 19); }
        &.gradient6 { background-color: rgb(158, 26, 22); }
    }

    .symbol-container {
        display: flex;
        align-items: flex-end !important;
    }

    .symbol-marker {
        $width: 35px;
        $height: 46px;
        $scale: 0.65;

        background: url("../assets/markers_custom.png") no-repeat 0 0;
        width: $width;
        height: $height;
        transform: scale($scale);
        margin: (-$height * (1 - $scale) / 2) (-$width * (1 - $scale) / 2);
        text-align: center;
        color: white;

        i.fa {
            margin-top: 9px;
            margin-left: 2px;
            font-size: 16px;
        }

        &.marker-scary {
            background-position: -36px 0;
        }

        &.marker-regular {
            background-position: -180px 0;
        }
    }
</style>
