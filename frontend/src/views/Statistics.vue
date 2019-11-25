<template>
    <div class="container content fcp-container">
        <h2>Statistics for
            <b-select v-model="selectedLocation" :loading="!dataLoaded" style="display: inline-block; position: absolute; margin-left: 13px;">
                <option v-for="location in locations" :value="location.name">
                    {{ location.name }}
                </option>
            </b-select>
        </h2>

        <div class="wrapper">
            <p>
                Anzahl der gefahrenen Kilometer als Animation/Grafik?<br>
                Gesparte CO2 Austöße
                <br>
                Rides: {{ ridesStatistics.rideCount || "" }}
            </p>

            <hr>

            <div class="columns">
                <div class="column">
                    Incidentstypen
                    <apexchart type=pie width=100% :options="chartOptions(incidentsStatistics.incidentTypeLabels)" :series="incidentsStatistics.incidentTypeData"/>
                </div>
                <div class="column">
                    Scary / Not scary
                    <apexchart type=donut width=100% :options="chartOptions(['Scary', 'Not scary'])" :series="[incidentsStatistics.countOfScary, incidentsStatistics.incidentCount]"/>
                </div>
                <div class="column">
                    Verkehrsteilnehmer
                    <apexchart type=pie width=100% :options="chartOptions(incidentsStatistics.participantTypeLabels)" :series="incidentsStatistics.participantTypeData"/>
                </div>
            </div>

            <hr>

            <div class="columns">
                <div class="column">
                    Profildaten: <br>
                    - männlich/weiblich-Verteilung <br>
                    - Alter-Verteilung
                </div>
                <div class="column">
                    Strecke:

                    <div v-if="dataLoaded">
                        <div>
                            Average speed: {{ (ridesStatistics.accumulatedDistance / ridesStatistics.accumulatedDuration).toFixed(2) }} km/h
                        </div>

                        <div>
                            Average distance: {{ (ridesStatistics.accumulatedDistance / ridesStatistics.rideCount).toFixed(2) }} km,
                            Total distance: {{ ridesStatistics.accumulatedDistance.toFixed(2) }} km
                        </div>
                        <div>
                            Average duration: {{ (ridesStatistics.accumulatedDuration / ridesStatistics.rideCount).toFixed(2) }} min,
                            Total duration: {{ ridesStatistics.accumulatedDuration.toFixed(2) }} min
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>

<script>
import VueApexCharts from "vue-apexcharts";

export default {
    name: "statistics",
    components: {
        apexchart: VueApexCharts,
    },
    props: {
        locations: Array,
    },
    data() {
        return {
            dataLoaded: false,
            selectedLocation: "Berlin",
            ridesStatistics: {},
            incidentsStatistics: {
                incidentCount: 0,
                countOfScary: 0,
                incidentTypeLabels: [],
                incidentTypeData: [],
                participantTypeLabels: [],
                participantTypeData: [],
            },
        };
    },
    methods: {
        loadData() {
            this.dataLoaded = false;

            setTimeout(() => {
                fetch("http://localhost:8080/statisticsDebug?region=" + this.selectedLocation)
                    .then(r => r.json())
                    .then(r => {
                        this.dataLoaded = true;
                        this.ridesStatistics = r.ridesStatistics;
                        this.incidentsStatistics = r.incidentsStatistics;
                        console.log(r);
                    });
            }, 500);
        },
        chartOptions(labels) {
            return {
                labels: labels,
            };
        }
    },
    watch: {
        selectedLocation: function (val, oldVal) {
            if (val !== oldVal) this.loadData();
        },
    },
    mounted() {
        this.loadData();
    },
};
</script>

<style lang="scss">
    .container {
        width: 100%;
    }
</style>
