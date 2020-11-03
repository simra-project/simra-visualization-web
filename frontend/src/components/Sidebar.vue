<template>
    <div class="sidebar" :class="{'small': small, 'resizable': !small}">
        <div class="entries">
            <SidebarEntry :title="$t('sidebar.rides')"
                          icon="fa-biking"
                          :icon-color="'#156ec7'"
                          :is-small="small"
                          :class="{'selected': value === config.viewModes.RIDES}"
                          @entryClicked="switchToView(config.viewModes.RIDES)">
                <div class="field">
                    <b-radio v-model="computedSubViewMode"
                             :native-value="subViewMode === config.subViewModes.RIDES_ORIGINAL ? config.subViewModes.RIDES_ORIGINAL : config.subViewModes.RIDES_DENSITY_ALL">
                        {{ $t('ride.all') }}
                    </b-radio>
                </div>
                <div class="field">
                    <b-radio v-model="computedSubViewMode"
                             :native-value="config.subViewModes.RIDES_DENSITY_RUSHHOURMORNING">
                        {{ $t('ride.rushHourMorning') }}
                    </b-radio>
                </div>
                <div class="field">
                    <b-radio v-model="computedSubViewMode"
                             :native-value="config.subViewModes.RIDES_DENSITY_RUSHHOUREVENING">
                        {{ $t('ride.rushHourEvening') }}
                    </b-radio>
                </div>
                <div class="field">
                    <b-radio v-model="computedSubViewMode"
                             :native-value="config.subViewModes.RIDES_DENSITY_WEEKEND">
                        {{ $t('ride.weekend') }}
                    </b-radio>
                </div>
            </SidebarEntry>

            <SidebarEntry :title="$t('sidebar.incidents')"
                          icon="fa-car-crash"
                          :icon-color="'#1d917c'"
                          :is-small="small"
                          :class="{'selected': value === config.viewModes.INCIDENTS}"
                          @entryClicked="switchToView(config.viewModes.INCIDENTS)">
                <div class="entry-subtext">
                    {{ $t('sidebar.incidentsDescription') }}
                </div>
            </SidebarEntry>

            <SidebarEntry :title="$t('sidebar.surfaceQuality')"
                          icon="fa-road"
                          :icon-color="'#d63e12'"
                          :is-small="small"
                          :class="{'selected': value === config.viewModes.SURFACE_QUALITY}"
                          @entryClicked="switchToView(config.viewModes.SURFACE_QUALITY)">
                <div class="entry-subtext">
                    {{ $t('sidebar.surfaceQualityDescription') }}
                </div>
            </SidebarEntry>

            <SidebarEntry :title="$t('sidebar.relativeSpeed')"
                          icon="fa-tachometer-alt"
                          :icon-color="'#156ec7'"
                          :is-small="small"
                          :class="{'selected': value === config.viewModes.RELATIVE_SPEED}"
                          @entryClicked="switchToView(config.viewModes.RELATIVE_SPEED)">
                <div class="entry-subtext">
                    {{ $t('sidebar.relativeSpeedDescription') }}
                </div>
            </SidebarEntry>

            <SidebarEntry :title="$t('sidebar.stopTimes')"
                          icon="fa-traffic-light"
                          :icon-color="'#1d917c'"
                          :is-small="small"
                          :class="{'selected': value === config.viewModes.STOP_TIMES}"
                          @entryClicked="switchToView(config.viewModes.STOP_TIMES)">
                <div class="entry-subtext">
                    {{ $t('sidebar.stopTimesDescription') }}
                </div>
            </SidebarEntry>

            <SidebarEntry :title="$t('sidebar.boxAnalysis')"
                          icon="fa-draw-polygon"
                          :icon-color="'#d63e12'"
                          :is-small="small"
                          :class="{'selected': value === config.viewModes.BOX_ANALYSIS}"
                          @entryClicked="switchToView(config.viewModes.BOX_ANALYSIS)">
                <div class="entry-subtext">
                    {{ $t('sidebar.boxAnalysisDescription') }}
                </div>

                <div class="field" style="margin-top: 10px">
                    <b-radio v-model="computedSubViewMode"
                             :native-value="config.subViewModes.BOX_ANALYSIS_START">
                        {{ $t('boxAnalysis.start') }}
                    </b-radio>
                </div>
                <div class="field">
                    <b-radio v-model="computedSubViewMode"
                             :native-value="config.subViewModes.BOX_ANALYSIS_CONTAINS">
                        {{ $t('boxAnalysis.contains') }}
                    </b-radio>
                </div>
                <div class="field">
                    <b-radio v-model="computedSubViewMode"
                             :native-value="config.subViewModes.BOX_ANALYSIS_STOP">
                        {{ $t('boxAnalysis.end') }}
                    </b-radio>
                </div>
            </SidebarEntry>

            <SidebarEntry :title="$t('sidebar.tools')"
                          :icon="'fa-tools'"
                          :icon-color="'#156ec7'"
                          :is-small="small"
                          :class="{'selected': value === config.viewModes.TOOLS}"
                          @entryClicked="switchToView(config.viewModes.TOOLS)">
                <div class="entry-subtext">
                    {{ $t('sidebar.toolsDescription') }}
                </div>
            </SidebarEntry>
        </div>

        <div class="bottom-expand-button" @click="small = false" :title="$t('sidebar.expandMenu')">
            <i class="fas fa-lg fa-angle-double-right"></i>
        </div>
        <div class="bottom">
            <div class="bottom-settings" :title="small ? $t('sidebar.settings') : ''">
                <div class="bottom-icon">
                    <i class="fas fa-lg fa-cogs"></i>
                </div>
                <div class="bottom-text">
                    {{ $t('sidebar.settings') }}
                </div>
            </div>
            <div class="bottom-reduce-button" @click="small = true">
                <i class="fas fa-lg fa-angle-double-left"></i>
            </div>
        </div>
    </div>
</template>

<script>
import SidebarEntry from "@/components/SidebarEntry";
import Config from "@/constants";

export default {
    name: "Sidebar",
    components: { SidebarEntry },
    props: [
        'value',
        'subViewMode',
    ],
    data() {
        return {
            config: Config,
            small: false,
        }
    },
    methods: {
        switchToView(viewId) {
            if (this.value === viewId) {
                this.$emit('input', Config.viewModes.NONE);
            } else {
                this.$emit('input', viewId);
            }
        }
    },
    watch: {
        small: function (newValue, oldValue) {
            if (newValue !== oldValue) this.$emit('size-changed');
        },
    },
    computed: {
        computedSubViewMode: {
            get() {
                return this.subViewMode;
            },
            set(value) {
                this.$emit('update:sub-view-mode', value);
            }
        }
    }
};
</script>

<style lang="scss">
    .sidebar {
        width: 370px;//240px;
        flex-shrink: 0;
        background-color: #fafafa;//#eaeaea;
        border-right: 1px solid rgba(0, 0, 0, 0.1);
        overflow-y: scroll;
        height: calc(100vh - 3.25rem - 1px);

        &.resizable {
            min-width: 240px;
            max-width: 50%;
            resize: horizontal;
            overflow-x: hidden;
            overflow-y: hidden;
        }
    }

    .entries {
        padding-top: 8px;
        height: calc(100% - 53px);
        overflow-y: auto;
        overflow-x: hidden;
    }

    .bottom {
        height: 52px;
        display: flex;
        align-items: center;
        border-top: 1px solid rgba(0, 0, 0, 0.1);
        border-bottom: 1px solid rgba(0, 0, 0, 0.1);

        .bottom-settings {
            display: flex;
            align-items: center;
            padding: 12px 8px;
            width: 100%;

            &:hover, &:active {
                background-color: #eaeaea;
                cursor: pointer;
            }

            .bottom-icon {
                width: 60px;
                flex-shrink: 0;
                padding-right: 6px;
                text-align: center;
            }

            .bottom-text {
                width: 100%;
                color: black;
                font-size: 18px;
                font-family: "Segoe UI VSS (Regular)", "Segoe UI", "-apple-system", BlinkMacSystemFont, Roboto, "Helvetica Neue", Helvetica, Ubuntu, Arial, sans-serif, "Apple Color Emoji", "Segoe UI Emoji", "Segoe UI Symbol";
                user-select: none;
            }
        }

        .bottom-reduce-button {
            height: 100%;
            width: 60px;
            flex-shrink: 0;
            display: flex;
            align-items: center;
            justify-content: center;

            &:hover, &:active {
                background-color: #eaeaea;
                cursor: pointer;
                color: #156ec7;
            }
        }
    }

    .bottom-expand-button {
        height: 52px;
        width: 70px;
        display: none;
        align-items: center;
        justify-content: center;

        &:hover, &:active {
            background-color: #eaeaea;
            cursor: pointer;
            color: #156ec7;
        }
    }

    .sidebar.small {
        width: 71px;
        overflow: hidden;

        .entries {
            height: calc(100% - 104px);
        }

        .entry {
            .entry-label .entry-text, .entry-content {
                display: none;
            }
        }

        .bottom .bottom-settings .bottom-text {
            display: none;
        }

        .bottom .bottom-reduce-button {
            display: none;
        }

        .bottom-expand-button {
            display: flex;
        }
    }
</style>
