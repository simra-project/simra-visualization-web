<template>
    <div class="map-filters">
        <hr class="hr-text" data-content="FILTERS">

        <template v-if="viewMode === 0">
            <b-field label="Ride has Incident">
                <b-select v-model="filterRideHasIncident">
                    <option :value="null">All rides</option>
                    <option :value="true">Only rides with incidents</option>
                    <option :value="false">Only rides without incidents</option>
                </b-select>
            </b-field>

            <b-field label="Weekday">
                <b-select v-model="filterRideWeekday">
                    <option :value="null">Any weekday</option>
                    <option v-for="weekday in weekdays" :value="weekday[0]">{{ weekday[1] }}</option>
                </b-select>
            </b-field>

            <b-field grouped>
                <b-field label="From hour">
                    <b-select v-model="filterRideFromHour">
                        <option :value="null">Any</option>
                        :   <option v-for="hour in fromHours" :value="hour">{{ hour }}:00</option>
                    </b-select>
                </b-field>
                <b-field label="To hour">
                    <b-select v-model="filterRideToHour">
                        <option :value="null">Any</option>
                        <option v-for="hour in toHours" :value="hour">{{ hour }}:00</option>
                    </b-select>
                </b-field>
            </b-field>
        </template>

        <template v-else>
            <b-field label="Scary / Regular Incidents" style="margin-bottom: 0.5rem;">
                <b-checkbox v-model="filterIncidentScary" @change.native="incidentsChanged">Scary Incidents</b-checkbox>
            </b-field>

            <b-field :type="{ 'is-danger': !filterIncidentScary && !filterIncidentRegular }"
                     :message="{ 'You have to choose at least one of these options': !filterIncidentScary && !filterIncidentRegular }">
                <b-checkbox v-model="filterIncidentRegular" @change.native="incidentsChanged">Regular Incidents</b-checkbox>
            </b-field>

            <b-field label="Incident Type">
                <b-select v-model="filterIncidentType" @change.native="incidentsChanged">
                    <option :value="null">Any type</option>
                    <option v-for="type in incidentTypes()" :value="type.id">{{ type.name }}</option>
                </b-select>
            </b-field>

            <b-field label="Incident Participant">
                <b-select v-model="filterIncidentParticipant" @change.native="incidentsChanged">
                    <option :value="null">Any participant</option>
                    <option v-for="participant in incidentParticipants()" :value="participant.id">{{ participant.name }}</option>
                </b-select>
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
        </template>
    </div>
</template>

<script>
import { IncidentUtils } from "@/services/IncidentUtils";

export default {
    name: "MapFilters",
    props: {
        viewMode: Number,
    },
    data() {
        return {
            filterRideHasIncident: null,
            filterRideWeekday: null,
            filterRideFromHour: null,
            filterRideToHour: null,
            filterIncidentScary: true,
            filterIncidentRegular: true,
            filterIncidentType: null,
            filterIncidentParticipant: null,
            filterIncidentWeekday: null,
            filterIncidentHours: [0, 24],
            weekdays: [["Mo.", "Monday"], ["Di.", "Tuesday"], ["Mi.", "Wednesday"], ["Do.", "Thursday"], ["Fr.", "Friday"], ["Sa.", "Saturday"], ["So.", "Sunday"]],
        }
    },
    methods: {
        incidentTypes: () => IncidentUtils.getTypes(),
        incidentParticipants: () => IncidentUtils.getParticipants(),
        incidentsChanged() {
            // Both checkboxes shouldn't be unselected, not applying filters
            if (!this.filterIncidentScary && !this.filterIncidentRegular) return;

            console.log("Filters changed!");
            this.$emit('incidents-changed');
        },
        getIncidentFilters() {
            // If a filter-value is the default, don't apply that filter
            return {
                scary: this.filterIncidentScary && this.filterIncidentRegular ? null : this.filterIncidentScary,
                incidents: this.filterIncidentType,
                participants: this.filterIncidentParticipant != null ? IncidentUtils.participantToBoolArray(this.filterIncidentParticipant).join(",") : null,
                weekdays: this.filterIncidentWeekday != null ? [this.filterIncidentWeekday] : null,
                fromMinutesOfDay: this.filterIncidentHours[0] !== 0 ? this.filterIncidentHours[0] * 60 : null,
                untilMinutesOfDay: this.filterIncidentHours[1] !== 24 ? this.filterIncidentHours[1] * 60 : null,
            };
        },
    },
    watch: {
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
    }
</style>
