<template>
    <div class="overlay legend" :class="{ viewModeRides: viewMode === 0, viewModeIncidents: viewMode === 1}">
        <template v-if="viewMode === 0">
            <div class="color-box c1"></div>
            <div class="color-box c2"></div>
            <div class="color-box c3"></div>
            <div class="text-box"> Bikers per street segment</div>
        </template>

        <template v-else>
            <div class="marker marker-scary"><i class="fa fa-car"/></div>
            <div class="text-box" style="break-after: page">Scary Incident</div>

            <div class="marker marker-regular"><i class="fa fa-car"/></div>
            <div class="text-box">Regular Incident</div>
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
        display: flex;
        padding: 6px 8px;
        border: 1px solid #b5b5b5cc;
        -webkit-box-shadow: none;
        box-shadow: none;
        background-color: white;

        div + div {
            margin-left: 4px;
        }

        .text-box {
            font-size: 14px;
            margin-left: 6px;
            align-self: center;
        }

        &.viewModeRides {
            align-items: end;

            .color-box {
                width: 15px;
                height: 15px;
                margin-bottom: 3px;

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
        }

        &.viewModeIncidents {
            flex-wrap: wrap;
            width: 60%;

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
</style>
