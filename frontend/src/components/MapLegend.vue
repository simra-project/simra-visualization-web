<template>
    <div class="overlay legend" :class="{viewModeIncidents: viewMode === 1 || viewMode === 3}">
        <div v-if="viewMode === 0" class="legend-row viewModeRides">
            <div class="color-box color-box-rides c1"></div>
            <div class="color-box color-box-rides c2"></div>
            <div class="color-box color-box-rides c3"></div>
            <div class="text-box"> Bikers per street segment</div>
        </div>

        <div v-else-if="viewMode === 1 || viewMode === 3" class="legend-row viewModeIncidents">
            <div class="marker marker-scary"><i class="fa fa-car"/></div>
            <div class="text-box" style="break-after: page">Scary Incident</div>

            <div class="marker marker-regular"><i class="fa fa-car"/></div>
            <div class="text-box">Regular Incident</div>
        </div>

        <template v-else-if="viewMode === 2">
            <div class="legend-row viewModeRides">
                <div class="color-box color-box-combined c1"></div>
                <div class="color-box color-box-combined c2"></div>
                <div class="color-box color-box-combined c3"></div>
                <div class="color-box color-box-combined c4"></div>
                <div class="text-box"> Incidents per street segment (color)</div>
            </div>
            <div class="legend-row viewModeRides">
                <div class="color-box color-box-combined-gray c1"></div>
                <div class="color-box color-box-combined-gray c2"></div>
                <div class="color-box color-box-combined-gray c3"></div>
                <div class="color-box color-box-combined-gray c4"></div>
                <div class="text-box"> Bikers per street segment (line width)</div>
            </div>

        </template>
    </div>
</template>

<script>
export default {
    name: "MapLegend",
    props: {
        viewMode: Number,
    },
};
</script>

<style lang="scss">
    .legend {
        padding: 6px 8px;
        border: 1px solid #b5b5b5cc;
        -webkit-box-shadow: none;
        box-shadow: none;
        background-color: white;



        &.viewModeIncidents {
            width: 60%;
            margin-left: auto;
        }

        .legend-row {
            display: flex;

            .text-box {
                font-size: 14px;
                margin-left: 6px;
                align-self: center;
            }

            &.viewModeRides {
                align-items: flex-end;

                .color-box {
                    width: 15px;
                    height: 15px;
                    margin-bottom: 3px;

                    & + .color-box {
                        margin-left: 4px;
                    }

                    &.color-box-rides {
                        &.c1 {
                            background-color: hsl(190, 71%, 53%);
                            opacity: 0.8;
                            height: 7px;
                        }

                        &.c2 {
                            background-color: hsl(215, 71%, 53%);
                            opacity: 0.9;
                            height: 11px;
                        }

                        &.c3 {
                            background-color: hsl(240, 71%, 53%);
                        }
                    }

                    &.color-box-combined {
                        &.c1 {
                            background-color: #84ca50;
                        }

                        &.c2 {
                            background-color: #ede202;
                        }

                        &.c3 {
                            background-color: #f07d02;
                        }

                        &.c4 {
                            background-color: #9e1313;
                        }
                    }

                    &.color-box-combined-gray {
                        background-color: gray !important;

                        &.c1 {
                            height: 3px;
                        }

                        &.c2 {
                            height: 7px;
                        }

                        &.c3 {
                            height: 11px;
                        }
                    }
                }
            }

            &.viewModeIncidents {
                flex-wrap: wrap;

                .marker {
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
            }
        }
    }
</style>
