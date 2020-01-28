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
                    <option v-for="weekday in weekdays" :value="weekday">{{ weekday }}</option>
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
            <b-field label="Is scary">
                <b-select v-model="filterIncidentIsScary">
                    <option :value="null">All incidents</option>
                    <option :value="true">Only scary incidents</option>
                    <option :value="false">Only regular incidents</option>
                </b-select>
            </b-field>

            <b-field label="Incident Type">
                <b-select v-model="filterIncidentType">
                    <option :value="null">Any type</option>
                    <option v-for="type in incidentTypes()" :value="type.id">{{ type.name }}</option>
                </b-select>
            </b-field>

            <b-field label="Incident Participant">
                <b-select v-model="filterIncidentParticipant">
                    <option :value="null">Any participant</option>
                    <option v-for="participant in incidentParticipants()" :value="participant.id">{{ participant.name }}</option>
                </b-select>
            </b-field>

            <b-field grouped>
                <b-field label="From hour">
                    <b-select v-model="filterIncidentFromHour">
                        <option :value="null">Any</option>
                        <option v-for="hour in fromHours" :value="hour">{{ hour }}:00</option>
                    </b-select>
                </b-field>
                <b-field label="To hour">
                    <b-select v-model="filterIncidentToHour">
                        <option :value="null">Any</option>
                        <option v-for="hour in toHours" :value="hour">{{ hour }}:00</option>
                    </b-select>
                </b-field>
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
            filterIncidentIsScary: null,
            filterIncidentType: null,
            filterIncidentParticipant: null,
            filterIncidentFromHour: null,
            filterIncidentToHour: null,
            weekdays: ['Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday', 'Saturday', 'Sunday'],
            fromHours: [0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23],
            toHours: [1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 0],
        }
    },
    methods: {
        incidentTypes: () => IncidentUtils.getTypes(),
        incidentParticipants: () => IncidentUtils.getParticipants(),
    }
};
</script>

<style lang="scss">

    .map-filters {
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
    }
</style>
