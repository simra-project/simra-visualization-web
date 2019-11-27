<template>
    <div class="container content fcp-container">
        <h2>Statistics for
            <b-select v-model="selectedLocation" :loading="!dataLoaded" style="display: inline-block; position: absolute; margin-left: 13px; margin-top: -5px;">
                <option v-for="location in locations" :value="location.name">
                    {{ location.name }}
                </option>
                <option value="X">All cities (TODO)</option>
            </b-select>
        </h2>

        <div class="wrapper" v-if="ridesStatistics.accumulatedDistance != null">
            <div class="top-text">
                Over
                <span class="highlight-text">
                            <ICountUp :delay="100" :endVal="485"/> bikers <!-- TODO -->
                        </span>
                in {{ selectedLocation }} cycled
                <span class="highlight-text">
                            <ICountUp :delay="400" :endVal="Math.floor(ridesStatistics.accumulatedDistance / 1000)"/> km
                        </span>
                so far reducing CO2 emissions by
                <span class="highlight-text">
                            <ICountUp :delay="700" :endVal="Math.floor(ridesStatistics.accumulatedSavedCO2)"/> kg</span>.
            </div>
            <div class="top-subtext">
                On average, one ride is {{ (ridesStatistics.accumulatedDistance / (1000 * ridesStatistics.rideCount)).toFixed(1) }} kilometers long and lasts {{ Math.floor(ridesStatistics.accumulatedDuration / (1000 * 60 * ridesStatistics.rideCount)) }} minutes.
                That's a speed of {{ (3.6 * ridesStatistics.accumulatedDistance / (ridesStatistics.accumulatedDuration / 1000)).toFixed(1) }} km/h on average.
            </div>

            <hr style="margin-bottom: 2.5rem;">

            <h3>Incidents</h3>
            <div class="columns incidents">
                <div class="column">
                    <h4>Incident Types</h4>
                    <apexchart type=donut width=100% :options="incidentTypes.options" :series="incidentTypes.data"/>
                </div>

                <div class="column">
                    <h4>Participants
                        <b-tooltip label="TODO: Erklärung Participants" style="vertical-align: bottom;">
                            <b-tag rounded>?</b-tag>
                        </b-tooltip>
                    </h4>
                    <apexchart type=donut width=100% :options="participantTypes.options" :series="participantTypes.data"/>
                </div>
                <div class="column">
                    <h4>Scary Incidents
                        <b-tooltip label="TODO: Erklärung scary" style="vertical-align: bottom;">
                            <b-tag rounded>?</b-tag>
                        </b-tooltip>
                    </h4>
                    <apexchart type=donut width=100% :options="chartOptions(['Not scary', 'Scary'])" :series="[incidentsStatistics.incidentCount, incidentsStatistics.countOfScary]"/>
                </div>
            </div>

            <hr>

            <h3>Users</h3>
            <div class="columns users">
                <div class="column">
                    <h4>Bike Types</h4>
                    <apexchart type=donut width=100% :options="bikeTypes.options" :series="bikeTypes.data"/>
                </div>
                <div class="column">
                    <h4>Age Distribution (todo)</h4>
                </div>
                <div class="column">
                    <h4>Gender (todo)</h4>
                    <apexchart type=donut width=100% :options="chartOptions(['Male', 'Female'])" :series="[63, 37]"/>
                </div>
            </div>

            <hr>

            <div class="bottom-info-text">
                The rides we store have no references left to the original user who sent it to us.<br>
                If you are a bicyclist too, download our app and start tracking!<br>
                <a class="button is-large is-light" href="https://www.mcc.tu-berlin.de/menue/forschung/projekte/simra/">
                    <strong>Download App</strong>
                </a>
            </div>
        </div>
        <div class="wrapper" v-else style="height: 250px">
            <div><b-loading :is-full-page="false" :active="true" :can-cancel="false"></b-loading></div> <!-- TODO -->
        </div>
    </div>
</template>

<script>
import VueApexCharts from "vue-apexcharts";
import ICountUp from "vue-countup-v2";

export default {
    name: "statistics",
    components: {
        apexchart: VueApexCharts,
        ICountUp,
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
            },
            incidentTypes: { labels: [], data: [], options: {} },
            participantTypes: { labels: [], data: [], options: {} },
            bikeTypes: { labels: [], data: [], options: {} },
        };
    },
    methods: {
        loadData() {
            this.dataLoaded = false;

            setTimeout(() => {
                let url = this.selectedLocation === "Berlin" ? "statistics" : "statisticsDebug";

                fetch("http://localhost:8080/" + url + "?region=" + this.selectedLocation)
                    .then(r => r.json())
                    .then(r => {
                        this.dataLoaded = true;
                        this.ridesStatistics = r.ridesStatistics;
                        this.incidentsStatistics = r.incidentsStatistics;
                        console.log(r);

                        this.incidentTypes = this.processData(4, r.incidentsStatistics.incidentTypeLabels, r.incidentsStatistics.incidentTypeData);
                        this.participantTypes = this.processData(4, r.incidentsStatistics.participantTypeLabels, r.incidentsStatistics.participantTypeData);
                        this.bikeTypes = this.processData(4, r.incidentsStatistics.bikeTypeLabels, r.incidentsStatistics.bikeTypeData);
                    });
            }, 500);
        },
        chartOptions(labels) {
            return {
                labels: labels,
                theme: {
                    palette: "palette2",
                    monochrome: {
                        enabled: false,
                    },
                },
                legend: {
                    position: "bottom",
                },
                tooltip: {
                    // enabled: false,
                },
            };
        },
        processData(showElementCount, labels, data) {
            let rData = [];
            let rLabels = [];

            while (rData.length < showElementCount) {
                let iMax = data.indexOf(Math.max(...data));
                if (data[iMax] < 1) break;

                rData.push(data.splice(iMax, 1)[0]);
                rLabels.push(labels.splice(iMax, 1)[0]);
            }

            let restSum = data.reduce((a, b) => a + b, 0);
            if (restSum > 0) {
                rData.push(restSum);
                rLabels.push("Other");
            }

            return { labels: rLabels, data: rData, options: this.chartOptions(rLabels) };
        },
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

    .wrapper {
        margin-bottom: 16px;

        .top-text {
            font-size: 32px;

            .highlight-text {
                background: linear-gradient(90deg, #ff8a00, #e52e71);
                -webkit-background-clip: text;
                -webkit-text-fill-color: transparent;
            }
        }

        .top-subtext {
            font-size: 26px;
            color: gray;
        }

        .columns {
            justify-content: space-between;

            .column {
                &.incidents, &.users {
                    max-width: 360px;
                }

                h4 {
                    text-align: center;
                    font-weight: normal;
                    margin-bottom: 8px;
                }
            }
        }

        .bottom-info-text {
            text-align: center;
            font-size: 26px;
            color: gray;
            padding: 30px;

            .button {
                margin-top: 22px;
                border: 1px solid #ddd;
            }
        }
    }
</style>
