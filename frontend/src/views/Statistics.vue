<template>
    <div class="container content fcp-container">
        <h2>{{ $t('statistics.title') }}
            <b-select v-model="selectedRegion" :loading="!dataLoaded" style="display: inline-block; position: absolute; margin-left: 13px; margin-top: -5px;">
                <option v-for="region in regions" :value="region">
                    {{ region }}
                </option>
            </b-select>
        </h2>

        <div class="wrapper" v-if="statistics.r_meters != null">
            <div class="top-text">
                {{ $t('statistics.toptext.over') }}
                <span class="highlight-text">
                    <ICountUp :delay="100" :endVal="statistics.p_count"/> {{ $t('statistics.toptext.bicyclists') }}
                </span>
                in {{ selectedRegion }} {{ $t('statistics.toptext.cycled') }}
                <span class="highlight-text">
                    <ICountUp :delay="400" :endVal="Math.floor(statistics.r_meters / 1000)"/> km
                </span>
                <span v-html="$t('statistics.toptext.soFarReducingCO2EmissionsBy1')" />&nbsp;
                <span class="highlight-text">
                    <ICountUp :delay="700" :endVal="Math.floor(3781.43)"/> kg
                </span>
                {{ $t('statistics.toptext.soFarReducingCO2EmissionsBy2') }}
            </div>
            <div class="top-subtext">
                {{ $t('statistics.subtext.1', [(statistics.r_avg_distance / 1000).toFixed(2), Math.floor(statistics.r_avg_duration / 60)]) }}
                {{ $t('statistics.subtext.2', [statistics.r_avg_velocity.toFixed(1)]) }}
            </div>

            <hr style="margin-bottom: 2.5rem;">

            <h3>{{ $t('statistics.incidents.incidents') }}</h3>
            <div class="columns incidents">
                <div class="column">
                    <h4>{{ $t('statistics.incidents.incidentTypes') }}</h4>
                    <apexchart type=donut width=100% :options="incidentTypes.options" :series="incidentTypes.data"/>
                </div>
                <div class="column">
                    <h4>{{ $t('statistics.incidents.scaryIncidents') }}
                        <b-tooltip :label="$t('statistics.incidents.scaryIncidentsHint')" type="is-light" style="vertical-align: bottom;">
                            <b-tag rounded>?</b-tag>
                        </b-tooltip>
                    </h4>
                    <apexchart type=donut width=100% :options="{...chartOptions([$t('statistics.incidents.notScary'), $t('statistics.incidents.scary')]), colors: ['#3f51b5', '#e52e71'] }" :series="[statistics.i_count, statistics.i_count_scary]"/>
                </div>
                <div class="column">
                    <h4>{{ $t('statistics.incidents.participants') }}
                        <b-tooltip :label="$t('statistics.incidents.participantsHint')" type="is-light" style="vertical-align: bottom;">
                            <b-tag rounded>?</b-tag>
                        </b-tooltip>
                    </h4>
                    <apexchart type=donut width=100% :options="participantTypes.options" :series="participantTypes.data"/>
                </div>
            </div>

            <hr>

            <h3>{{ $t('statistics.users.users') }}</h3>
            <div class="columns reset-columns">
                <div class="column"><h4>{{ $t('statistics.users.bikeTypes') }}</h4></div>
                <div class="column"><h4>{{ $t('statistics.users.ageDistribution') }}</h4></div>
                <div class="column"><h4>{{ $t('statistics.users.gender') }}</h4></div>
            </div>

            <div class="columns users">
                <div class="column">
                    <apexchart type=donut width=100% :options="bikeTypes.options" :series="bikeTypes.data"/>
                </div>
                <div class="column" style="align-self: center">
                    <apexchart type=bar width=100% :options="ageDistributionOptions" :series="ageDistributionData"/>
                </div>
                <div class="column">
                    <apexchart type=donut width=100% :options="chartOptions([$t('statistics.users.genderList.male'), $t('statistics.users.genderList.female'), $t('statistics.users.genderList.other')])" :series="[statistics.p_gender_male, statistics.p_gender_female, statistics.p_gender_other]"/>
                </div>
            </div>
        </div>
        <div class="wrapper" v-else style="height: calc(100vh - 57px - 68px - 80px)">
            <b-loading :is-full-page="false" :active="true" :can-cancel="false"/>
        </div>
    </div>
</template>

<script>
import VueApexCharts from "vue-apexcharts";
import ICountUp from "vue-countup-v2";
import {IncidentUtils} from "@/services/IncidentUtils";

export default {
    name: "statistics",
    components: {
        apexchart: VueApexCharts,
        ICountUp,
    },
    props: {
        regions: Array,
    },
    data() {
        return {
            dataLoaded: false,
            selectedRegion: null,
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
            if (this.selectedRegion === null) return;
            this.dataLoaded = false;

            setTimeout(() => {
                fetch(`http://207.180.205.80:8000/api/statistics/${this.selectedRegion}/`)
                    .then(r => r.json())
                    .then(r => {
                        this.dataLoaded = true;
                        this.statistics = r;

                        let incidentData = [r.i_incident_0, r.i_incident_1, r.i_incident_2, r.i_incident_3, r.i_incident_4, r.i_incident_5, r.i_incident_6, r.i_incident_7, r.i_incident_8];
                        this.incidentTypes = this.processData(4, IncidentUtils.getTypes().map(x => this.$t(x.translationKey)), incidentData);

                        let participantData = [/*r.i_itype_0,*/ r.i_itype_1, r.i_itype_2, r.i_itype_3, r.i_itype_4, r.i_itype_5, r.i_itype_6, r.i_itype_7, r.i_itype_8, r.i_itype_9, r.i_itype_10];
                        this.participantTypes = this.processData(4, IncidentUtils.getParticipants().map(x => this.$t(x.translationKey)), participantData);

                        let bikeData = [r.i_biketype_0, r.i_biketype_1, r.i_biketype_2, r.i_biketype_3, r.i_biketype_4, r.i_biketype_5, r.i_biketype_6, r.i_biketype_7, r.i_biketype_8];
                        this.bikeTypes = this.processData(4, IncidentUtils.getBikeTypes().map(x => this.$t(x.translationKey)), bikeData);

                        this.ageDistributionOptions.xaxis.categories = ["> 2004", "2000–2004", "1995–1999", "1990–1994", "1985–1989", "1980–1984", "1975–1979", "1970–1974", "1965–1969", "1960–1964", "1955–1959", "1950–1954", "< 1950"];
                        this.ageDistributionData = [
                            { name: this.$t('statistics.users.genderList.male'), data: [r.p_birth_1_male, r.p_birth_2_male, r.p_birth_3_male, r.p_birth_4_male, r.p_birth_5_male, r.p_birth_6_male, r.p_birth_7_male, r.p_birth_8_male, r.p_birth_9_male, r.p_birth_10_male, r.p_birth_11_male, r.p_birth_12_male, r.p_birth_13_male] },
                            { name: this.$t('statistics.users.genderList.female'), data: [r.p_birth_1_female, r.p_birth_2_female, r.p_birth_3_female, r.p_birth_4_female, r.p_birth_5_female, r.p_birth_6_female, r.p_birth_7_female, r.p_birth_8_female, r.p_birth_9_female, r.p_birth_10_female, r.p_birth_11_female, r.p_birth_12_female, r.p_birth_13_female] },
                            { name: this.$t('statistics.users.genderList.other'), data: [r.p_birth_1_other, r.p_birth_2_other, r.p_birth_3_other, r.p_birth_4_other, r.p_birth_5_other, r.p_birth_6_other, r.p_birth_7_other, r.p_birth_8_other, r.p_birth_9_other, r.p_birth_10_other, r.p_birth_11_other, r.p_birth_12_other, r.p_birth_13_other] },
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
                rLabels.push(this.$t('statistics.other'));
            }

            return { labels: rLabels, data: rData, options: this.chartOptions(rLabels) };
        },
    },
    watch: {
        selectedRegion: function (val, oldVal) {
            if (val !== oldVal) this.loadData();
        },
        regions: function (val, oldVal) {
            if (val !== null) {
                this.selectedRegion = val[0];
            }
        },
    },
    created() {
        if (this.regions !== null) {
            this.selectedRegion = this.regions[0];
        }
    },
};
</script>

<style lang="scss">
    .fcp-container {
        padding: 20px;

        & > .wrapper {
            background-color: white;
            border: 1px solid #ddd;
            padding: 16px 20px;
            margin-bottom: 60px;

            hr + h3 {
                margin-top: 0;
            }
        }
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
