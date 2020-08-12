<template>
    <div class="sidebar resizable">
        <div class="entries">
            <SidebarEntry title="Rides"
                          icon="fa-biking"
                          :iconColor="'#156ec7'"
                          :class="{'selected': value === config.VIEW_MODE_RIDES}"
                          @entryClicked="switchToView(config.VIEW_MODE_RIDES)">
                <MapFilters :view-mode="0" ref="filterRides"></MapFilters>
            </SidebarEntry>

            <SidebarEntry title="Incidents"
                          icon="fa-car-crash"
                          :iconColor="'#1d917c'"
                          :class="{'selected': value === config.VIEW_MODE_INCIDENTS}"
                          @entryClicked="switchToView(config.VIEW_MODE_INCIDENTS)">
                <MapFilters :view-mode="1" ref="filterIncidents"></MapFilters>
            </SidebarEntry>

            <SidebarEntry title="Combined"
                          icon="fa-layer-group"
                          :iconColor="'#d63e12'"
                          :class="{'selected': value === config.VIEW_MODE_COMBINED}"
                          @entryClicked="switchToView(config.VIEW_MODE_COMBINED)">
                <MapFilters :view-mode="2" ref="filterCombined"></MapFilters>
            </SidebarEntry>

            <SidebarEntry title="Box-Analysis"
                          icon="fa-draw-polygon"
                          :iconColor="'#156ec7'"
                          :class="{'selected': value === config.VIEW_MODE_BOX_ANALYSIS}"
                          @entryClicked="switchToView(config.VIEW_MODE_BOX_ANALYSIS)">
                <div class="entry-subtext">
                    Draw a shape on the map to see what bike rides went through it. Click on the shape to draw a new one.
                </div>
            </SidebarEntry>

            <SidebarEntry :title="'Tools'"
                          :icon="'fa-tools'"
                          :iconColor="'#1d917c'"
                          :class="{'selected': value === config.VIEW_MODE_TOOLS}"
                          @entryClicked="switchToView(config.VIEW_MODE_TOOLS)">
                <div class="entry-subtext">
                    Upload a ride to analyze it (drag the file on the map).
                </div>
            </SidebarEntry>
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
        }
    },
    methods: {
        switchToView(viewId) {
            if (this.value === viewId) {
                this.value = Config.VIEW_MODE_NONE;
            } else {
                this.value = viewId;
            }
            this.$emit('input', this.value);
        }
    }
};
</script>

<style scoped lang="scss">
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
    }
</style>
