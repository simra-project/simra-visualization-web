<template>
    <div class="sidebar" :class="{'small': small, 'resizable': !small}">
        <div class="entries">
            <SidebarEntry title="Rides"
                          icon="fa-biking"
                          :icon-color="'#156ec7'"
                          :is-small="small"
                          :class="{'selected': value === config.viewModes.RIDES}"
                          @entryClicked="switchToView(config.viewModes.RIDES)">
                <MapFilters :view-mode="0" ref="filterRides" @rides-changed="$emit('filters-changed')"></MapFilters>
            </SidebarEntry>

            <SidebarEntry title="Incidents"
                          icon="fa-car-crash"
                          :icon-color="'#1d917c'"
                          :is-small="small"
                          :class="{'selected': value === config.viewModes.INCIDENTS}"
                          @entryClicked="switchToView(config.viewModes.INCIDENTS)">
                <MapFilters :view-mode="1" ref="filterIncidents" @incidents-changed="$emit('filters-changed')"></MapFilters>
            </SidebarEntry>

            <SidebarEntry title="Combined"
                          icon="fa-layer-group"
                          :icon-color="'#d63e12'"
                          :is-small="small"
                          :class="{'selected': value === config.viewModes.COMBINED}"
                          @entryClicked="switchToView(config.viewModes.COMBINED)">
                <MapFilters :view-mode="2" ref="filterCombined" @rides-changed="$emit('filters-changed')"></MapFilters>
            </SidebarEntry>

            <SidebarEntry title="Box-Analysis"
                          icon="fa-draw-polygon"
                          :icon-color="'#156ec7'"
                          :is-small="small"
                          :class="{'selected': value === config.viewModes.BOX_ANALYSIS}"
                          @entryClicked="switchToView(config.viewModes.BOX_ANALYSIS)">
                <div class="entry-subtext">
                    Draw a shape on the map to see what bike rides went through it. Click on the shape to draw a new one.
                </div>
            </SidebarEntry>

            <SidebarEntry :title="'Tools'"
                          :icon="'fa-tools'"
                          :icon-color="'#1d917c'"
                          :is-small="small"
                          :class="{'selected': value === config.viewModes.TOOLS}"
                          @entryClicked="switchToView(config.viewModes.TOOLS)">
                <div class="entry-subtext">
                    Upload a ride to analyze it (drag the file on the map).
                </div>
            </SidebarEntry>
        </div>

        <div class="bottom-expand-button" @click="small = false" title="Expand menu">
            <i class="fas fa-lg fa-angle-double-right"></i>
        </div>
        <div class="bottom">
            <div class="bottom-settings" :title="small ? 'Settings' : ''">
                <div class="bottom-icon">
                    <i class="fas fa-lg fa-cogs"></i>
                </div>
                <div class="bottom-text">
                    Settings
                </div>
            </div>
            <div class="bottom-reduce-button" @click="small = true">
                <i class="fas fa-lg fa-angle-double-left"></i>
            </div>
        </div>
    </div>
</template>

<script>
import MapFilters from "@/components/MapFilters";
import SidebarEntry from "@/components/SidebarEntry";
import SidebarSubEntry from "@/components/SidebarSubEntry";
import Config from "@/constants";

export default {
    name: "Sidebar",
    components: { SidebarEntry, SidebarSubEntry, MapFilters },
    props: [
        'value',
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
                this.value = Config.viewModes.NONE;
            } else {
                this.value = viewId;
            }
            this.$emit('input', this.value);
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
        }
    }

    .entries {
        padding-top: 8px;
        height: calc(100% - 53px);
        overflow-y: scroll;
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
