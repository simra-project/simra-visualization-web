<template>
    <div class="container content fcp-container">
        <h2>Statistics for
            <b-select v-model="selectedLocation" :loading="!dataLoaded" style="display: inline-block; position: absolute; margin-left: 13px; margin-top: -5px;">
                <option v-for="location in locations" :value="location.name">
                    {{ location.name }}
                </option>
<!--                <option value="X">All cities (TODO)</option>-->
            </b-select>
        </h2>

        <div class="wrapper" v-if="statistics.accumulatedDistance != null">
            <div class="top-text">
                Over
                <span class="highlight-text">
                    <ICountUp :delay="100" :endVal="statistics.profileCount"/> bikers
                </span>
                in {{ selectedLocation }} cycled
                <span class="highlight-text">
                    <ICountUp :delay="400" :endVal="Math.floor(statistics.accumulatedDistance / 1000)"/> km
                </span>
                so far reducing CO<sub>2</sub> emissions by
                <span class="highlight-text">
                    <ICountUp :delay="700" :endVal="Math.floor(3781.43)"/> kg</span>.
            </div>
            <div class="top-subtext">
                On average, one ride is {{ (statistics.averageDistance / 1000).toFixed(2) }} kilometers long and lasts {{ Math.floor(statistics.averageDuration / (1000 * 60)) }} minutes.
                That's a speed of {{ statistics.averageSpeed.toFixed(1) }} km/h on average.
            </div>

            <hr style="margin-bottom: 2.5rem;">

            <h3>Incidents</h3>
            <div class="columns incidents">
                <div class="column">
                    <h4>Incident Types</h4>
                    <apexchart type=donut width=100% :options="incidentTypes.options" :series="incidentTypes.data"/>
                </div>
                <div class="column">
                    <h4>Scary Incidents
                        <b-tooltip label="A scary incident is one where the cyclist fears for their safety." type="is-light" style="vertical-align: bottom;">
                            <b-tag rounded>?</b-tag>
                        </b-tooltip>
                    </h4>
                    <apexchart type=donut width=100% :options="{...chartOptions(['Not scary', 'Scary']), colors: ['#3f51b5', '#e52e71'] }" :series="[statistics.incidentCount, statistics.incidentCountScary]"/>
                </div>
                <div class="column">
                    <h4>Participants
                        <b-tooltip label="The other participant in the incident." type="is-light" style="vertical-align: bottom;">
                            <b-tag rounded>?</b-tag>
                        </b-tooltip>
                    </h4>
                    <apexchart type=donut width=100% :options="participantTypes.options" :series="participantTypes.data"/>
                </div>
            </div>

            <hr>

            <h3>Users</h3>
            <div class="columns reset-columns">
                <div class="column"><h4>Bike Types</h4></div>
                <div class="column"><h4>Age Distribution</h4></div>
                <div class="column"><h4>Gender</h4></div>
            </div>

            <div class="columns users">
                <div class="column">
                    <apexchart type=donut width=100% :options="bikeTypes.options" :series="bikeTypes.data"/>
                </div>
                <div class="column" style="align-self: center">
                    <apexchart type=bar width=100% :options="ageDistributionOptions" :series="ageDistributionData"/>
                </div>
                <div class="column">
                    <apexchart type=donut width=100% :options="chartOptions(['Male', 'Female', 'Other'])" :series="[statistics.profileCountMale, statistics.profileCountFemale, statistics.profileCountOther]"/>
                </div>
            </div>

<!--            <hr>-->

<!--            <h3>Raw data</h3>-->
<!--            <table class="table is-striped is-hoverable is-bordered is-narrow">-->
<!--                <thead>-->
<!--                <tr>-->
<!--                    <th>Age Group</th>-->
<!--                    <th style="white-space: nowrap;"># Bikers</th>-->
<!--                    <th style="white-space: nowrap;"># Rides</th>-->
<!--                    <th>Total Distance</th>-->
<!--                    <th>Total Duration</th>-->
<!--                    <th>Average Distance</th>-->
<!--                    <th>Average Duration</th>-->
<!--                    <th>Average Speed</th>-->
<!--                    <th>#&nbsp;Scary Incidents</th>-->
<!--                    <th>Average # Scary Incidents</th>-->
<!--                    <th>Total CO<sub>2</sub> saved</th>-->
<!--                    <th>Average CO<sub>2</sub> saved</th>-->
<!--                </tr>-->
<!--                </thead>-->
<!--                <tbody>-->
<!--                <tr v-for="row in statistics.profileAgeGroupCrossData">-->
<!--                    <td><strong>{{ row.ageGroup }}</strong></td>-->
<!--                    <td>{{ row.bikerCount.toLocaleString('en') }}</td>-->
<!--                    <td>{{ row.rideCount.toLocaleString('en') }}</td>-->

<!--                    <td>{{ Math.floor(row.accumulatedDistance / 1000).toLocaleString('en') }} km</td>-->
<!--                    <td>{{ Math.floor(row.accumulatedDuration / (1000 * 60 * 60)).toLocaleString('en') }} h</td>-->

<!--                    <template v-if="row.rideCount > 0">-->
<!--                        <td>{{ (row.averageDistance / 1000).toFixed(2) }} km</td>-->
<!--                        <td>{{ Math.floor(row.averageDuration / (1000 * 60)) }} min</td>-->
<!--                        <td>{{ row.averageSpeed.toFixed(1) }} km/h</td>-->
<!--                    </template>-->
<!--                    <template v-else>-->
<!--                        <td>-</td>-->
<!--                        <td>-</td>-->
<!--                        <td>-</td>-->
<!--                    </template>-->

<!--                    <td>{{ row.scaryIncidentCount.toLocaleString('en') }}</td>-->
<!--                    <td>{{ row.rideCount > 0 ? row.averageScaryIncidentCount.toFixed(1).toLocaleString('en') : '-' }}</td>-->

<!--                    <td>{{ Math.floor(row.accumulatedSavedCO2).toLocaleString('en') }} kg</td>-->
<!--                    <td>{{ row.rideCount > 0 ? (Math.floor(row.averageSavedCO2).toLocaleString('en') + ' kg') : '-' }}</td>-->
<!--                </tr>-->
<!--                </tbody>-->
<!--                <tfoot>-->
<!--                <tr>-->
<!--                    <th><strong>Total</strong></th>-->
<!--                    <th>{{ statistics.profileAgeGroupCrossTotal.bikerCount.toLocaleString('en') }}</th>-->
<!--                    <th>{{ statistics.profileAgeGroupCrossTotal.rideCount.toLocaleString('en') }}</th>-->

<!--                    <th>{{ Math.floor(statistics.profileAgeGroupCrossTotal.accumulatedDistance / 1000).toLocaleString('en') }} km</th>-->
<!--                    <th>{{ Math.floor(statistics.profileAgeGroupCrossTotal.accumulatedDuration / (1000 * 60 * 60)).toLocaleString('en') }} h</th>-->

<!--                    <th>{{ (statistics.profileAgeGroupCrossTotal.averageDistance / 1000).toFixed(2) }} km</th>-->
<!--                    <th>{{ Math.floor(statistics.profileAgeGroupCrossTotal.averageDuration / (1000 * 60)) }} min</th>-->
<!--                    <th>{{ statistics.profileAgeGroupCrossTotal.averageSpeed.toFixed(1) }} km/h</th>-->

<!--                    <th>{{ statistics.profileAgeGroupCrossTotal.scaryIncidentCount.toLocaleString('en') }}</th>-->
<!--                    <th>{{ statistics.profileAgeGroupCrossTotal.averageScaryIncidentCount.toFixed(1).toLocaleString('en') }}</th>-->

<!--                    <th>{{ Math.floor(statistics.profileAgeGroupCrossTotal.accumulatedSavedCO2).toLocaleString('en') }} kg</th>-->
<!--                    <th>{{ Math.floor(statistics.profileAgeGroupCrossTotal.averageSavedCO2).toLocaleString('en') }} kg</th>-->
<!--                </tr>-->
<!--                </tfoot>-->
<!--            </table>-->

            <hr>

            <div class="bottom-info-text">
                The rides we store have no references left to the original user who sent it to us.<br>
                If you are a bicyclist too, download our app and start tracking!<br>
                <a class="button is-large is-light" href="https://www.mcc.tu-berlin.de/menue/forschung/projekte/simra/">
                    <strong>Download App</strong>
                </a>
            </div>
        </div>
        <div class="wrapper" v-else style="height: 300px">
            <b-loading :is-full-page="false" :active="true" :can-cancel="false"/>
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
            statistics: {
                incidentCount: 0,
                incidentCountScary: 0,
            },
            incidentTypes: { labels: [], data: [], options: {} },
            participantTypes: { labels: [], data: [], options: {} },
            bikeTypes: { labels: [], data: [], options: {} },
            ageDistributionOptions: {
                chart: {
                    stacked: true,
                    toolbar: {
                        show: false,
                    },
                },
                dataLabels: {
                    enabled: false,
                },
                legend: {
                    offsetY: -8,
                },
                plotOptions: {
                    bar: {
                        barHeight: "90%",
                    },
                },
                theme: {
                    palette: "palette2",
                },
                xaxis: {
                    categories: [],
                },
                yaxis: {
                    show: false,
                },
            },
            ageDistributionData: [],
        };
    },
    methods: {
        loadData() {
            this.dataLoaded = false;

            setTimeout(() => {
                fetch(process.env.VUE_APP_BACKEND_URL + "/statistics?region=" + this.selectedLocation)
                    .then(r => r.json())
                    .then(r => {
                        this.dataLoaded = true;
                        this.statistics = r;

                        this.incidentTypes = this.processData(4, r.incidentTypeLabels, r.incidentTypeData);
                        this.participantTypes = this.processData(4, r.incidentParticipantTypeLabels, r.incidentParticipantTypeData);
                        this.bikeTypes = this.processData(4, r.profileBikeTypeLabels, r.profileBikeTypeData);

                        this.ageDistributionOptions.xaxis.categories = r.profileAgeDistributionLabels;
                        this.ageDistributionData = [
                            { name: "Male", data: r.profileAgeDistributionDataMale },
                            { name: "Female", data: r.profileAgeDistributionDataFemale },
                            { name: "Other", data: r.profileAgeDistributionDataOther },
                        ];
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
                    enabled: true,
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

            &.reset-columns {
                margin-bottom: 0;

                .column {
                    padding: 0;
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

    .loading-overlay {
        height: 100%;
        position: static;

        .loading-background {
            background: transparent;
        }
    }
</style>
