<template>
    <div class="map-filters">
        <hr class="hr-text" data-content="FILTERS">

        <template v-if="viewMode === 0 || viewMode === 2">
<!--            <b-field label="Bike rides" style="margin-bottom: 0.5rem;">-->
<!--                <b-checkbox v-model="filterRideWithIncident" @change.native="ridesChanged">With Incidents</b-checkbox>-->
<!--            </b-field>-->

<!--            <b-field :type="{ 'is-danger': !filterRideWithIncident && !filterRideWithoutIncident }"-->
<!--                     :message="{ 'You have to choose at least one of these options': !filterRideWithIncident && !filterRideWithoutIncident }">-->
<!--                <b-checkbox v-model="filterRideWithoutIncident" @change.native="ridesChanged">Without Incidents</b-checkbox>-->
<!--            </b-field>-->

            <b-field label="Weekday">
                <b-select v-model="filterRideWeekday" @change.native="ridesChanged">
                    <option :value="null">Any weekday</option>
                    <option v-for="weekday in weekdays" :value="weekday[0]">{{ weekday[1] }}</option>
                </b-select>
            </b-field>

            <b-field label="Time of day">
                <b-slider v-model="filterRideHours" @change="ridesChanged"
                          :min="0" :max="24" :step="1" lazy rounded :custom-formatter="h => h + ':00'">
                    <template v-for="h in [4, 8, 12, 16, 20]">
                        <b-slider-tick :value="h" :key="h">{{ h }}:00</b-slider-tick>
                    </template>
                </b-slider>
            </b-field>

            <b-button class="reset-filters" v-if="hasRideFilters()" @click="resetRideFilters">
                Reset filters
            </b-button>
        </template>

        <template v-else-if="viewMode === 1 || viewMode === 3">
            <b-field label="Scary / Regular Incidents" style="margin-bottom: 0.5rem;">
                <b-checkbox v-model="filterIncidentScary" @change.native="incidentsChanged">Scary Incidents</b-checkbox>
            </b-field>

            <b-field :type="{ 'is-danger': !filterIncidentScary && !filterIncidentRegular }"
                     :message="{ 'You have to choose at least one of these options': !filterIncidentScary && !filterIncidentRegular }">
                <b-checkbox v-model="filterIncidentRegular" @change.native="incidentsChanged">Regular Incidents</b-checkbox>
            </b-field>

            <b-field label="Incident Type">
                <MapFiltersMultiSelect v-model="filterIncidentTypes" @input="incidentsChanged" :options="incidentTypes()" empty-label="Any type"/>
            </b-field>

            <b-field label="Incident Participant">
                <MapFiltersMultiSelect v-model="filterIncidentParticipants" @input="incidentsChanged" :options="incidentParticipants()" empty-label="Any participant"/>
            </b-field>

            <b-field label="Weekday">
                <b-select v-model="filterIncidentWeekday" @change.native="incidentsChanged">
                    <option :value="null">Any weekday</option>
                    <option v-for="weekday in weekdays" :value="weekday[0]">{{ weekday[1] }}</option>
                </b-select>
            </b-field>

            <b-field label="Time of day">
                <b-slider v-model="filterIncidentHours" @change="incidentsChanged"
                          :min="0" :max="24" :step="1" lazy rounded :custom-formatter="h => h + ':00'">
                    <template v-for="h in [4, 8, 12, 16, 20]">
                        <b-slider-tick :value="h" :key="h">{{ h }}:00</b-slider-tick>
                    </template>
                </b-slider>
            </b-field>

            <b-button class="reset-filters" v-if="hasIncidentFilters()" @click="resetIncidentFilters">
                Reset filters
            </b-button>
        </template>
    </div>
</template>

<script>
import { IncidentUtils } from "@/services/IncidentUtils";
import MapFiltersMultiSelect from "@/components/MapFiltersMultiSelect";

export default {
    name: "MapFilters",
    components: { MapFiltersMultiSelect },
    props: {
        viewMode: Number,
    },
    data() {
        return {
            filterRideWithIncident: true,
            filterRideWithoutIncident: true,
            filterRideWeekday: null,
            filterRideHours: [0, 24],
            filterIncidentScary: true,
            filterIncidentRegular: true,
            filterIncidentTypes: [],
            filterIncidentParticipants: [],
            filterIncidentWeekday: null,
            filterIncidentHours: [0, 24],
            weekdays: [["Mon", "Monday"], ["Tue", "Tuesday"], ["Wed", "Wednesday"], ["Thu", "Thursday"], ["Fri", "Friday"], ["Sat", "Saturday"], ["Sun", "Sunday"]],
            timerRides: null,
            timerIncidents: null,
        }
    },
    methods: {
        incidentTypes: () => IncidentUtils.getTypes(),
        incidentParticipants: () => IncidentUtils.getParticipants(),
        ridesChanged() {
            // Both checkboxes shouldn't be unselected, not applying filters
            if (!(this.filterRideWithIncident || this.filterRideWithoutIncident)) return;

            console.log("Ride-Filters changed!");
            clearTimeout(this.timerRides);
            this.timerRides = setTimeout(() => this.$emit("rides-changed"), 500);
        },
        incidentsChanged() {
            // Both checkboxes shouldn't be unselected, not applying filters
            if (!(this.filterIncidentScary || this.filterIncidentRegular)) return;

            console.log("Incident-Filters changed!");
            clearTimeout(this.timerIncidents);
            this.timerIncidents = setTimeout(() => this.$emit("incidents-changed"), 500);
        },
        getRideFilters() {
            // If a filter-value is the default, don't apply that filter
            return {
                weekdays: this.filterRideWeekday != null ? [this.filterRideWeekday] : null,
                fromMinutesOfDay: this.filterRideHours[0] !== 0 ? this.filterRideHours[0] * 60 : null,
                untilMinutesOfDay: this.filterRideHours[1] !== 24 ? this.filterRideHours[1] * 60 : null,
            };
        },
        getIncidentFilters() {
            // If a filter-value is the default, don't apply that filter
            return {
                scary: this.filterIncidentScary && this.filterIncidentRegular ? null : this.filterIncidentScary,
                incidents: this.filterIncidentTypes.length > 0 ? this.filterIncidentTypes : null,
                participants: this.filterIncidentParticipants.length > 0 ? IncidentUtils.participantsToBoolArray(this.filterIncidentParticipants).join(",") : null,
                weekdays: this.filterIncidentWeekday != null ? [this.filterIncidentWeekday] : null,
                fromMinutesOfDay: this.filterIncidentHours[0] !== 0 ? this.filterIncidentHours[0] * 60 : null,
                untilMinutesOfDay: this.filterIncidentHours[1] !== 24 ? this.filterIncidentHours[1] * 60 : null,
            };
        },
        hasRideFilters() {
            return this.filterRideWithIncident !== true ||
                   this.filterRideWithoutIncident !== true ||
                   this.filterRideWeekday !== null ||
                   this.filterRideHours[0] !== 0 ||
                   this.filterRideHours[1] !== 24;
        },
        hasIncidentFilters() {
            return this.filterIncidentScary !== true ||
                   this.filterIncidentRegular !== true ||
                   this.filterIncidentTypes.length !== 0 ||
                   this.filterIncidentParticipants.length !== 0 ||
                   this.filterIncidentWeekday !== null ||
                   this.filterIncidentHours[0] !== 0 ||
                   this.filterIncidentHours[1] !== 24;
        },
        resetRideFilters() {
            this.filterRideWithIncident = true;
            this.filterRideWithoutIncident = true;
            this.filterRideWeekday = null;
            this.filterRideHours = [0, 24];
            this.ridesChanged();
        },
        resetIncidentFilters() {
            this.filterIncidentScary = true;
            this.filterIncidentRegular = true;
            this.filterIncidentTypes = [];
            this.filterIncidentParticipants = [];
            this.filterIncidentWeekday = null;
            this.filterIncidentHours = [0, 24];
            this.incidentsChanged();
        }
    },
    watch: {
        filterRideHours(value, oldValue) {
            // Don't allow selecting the same hour for the range start and end
            if (value[0] === value[1]) {
                if (value[0] < 24) value[1] += 1;
                else value[0] -=1;
            }
        },
        filterIncidentHours(value, oldValue) {
            // Don't allow selecting the same hour for the range start and end
            if (value[0] === value[1]) {
                if (value[0] < 24) value[1] += 1;
                else value[0] -=1;
            }
        }
    }
};
</script>

<style lang="scss">

    .map-filters {
        padding-bottom: 6px;

        .hr-text {
            line-height: 1em;
            position: relative;
            outline: 0;
            border: 0;
            color: #999;
            text-align: center;
            height: 1.5em;
            opacity: .5;
            background-color: white;
            margin-bottom: 10px;
            margin-top: -10px;

            &:before {
                content: '';
                background: #999;
                position: absolute;
                left: 0;
                top: 50%;
                width: 100%;
                height: 1px;
            }

            &:after {
                content: attr(data-content);
                position: relative;
                display: inline-block;
                padding: 0 .5em;
                line-height: 1.5em;
                background-color: white;
            }
        }

        label.label {
            margin-bottom: 4px;
        }

        .b-checkbox.checkbox {
            font-size: 16px;
        }

        .b-slider {
            padding: 0 8px;
        }

        .reset-filters {
            width: 100%;
            margin-top: 16px;
        }
    }
</style>
