<template>
    <div>
        <strong v-if="incident.scary">{{ t('incident.scary') }}</strong> {{ t('incident.incident') }}
        <template v-if="utils.isRecentDate(incident)"> {{ t('incident.on') }} {{ utils.getDate(incident) }}</template>
        <br>
        <hr>

        {{ t('incident.incidentType') }}: <strong>{{ t(utils.getType(incident)) }}</strong><br>
        {{ t('incident.participant') }}: <strong>{{ t(utils.getParticipant(incident)) }}</strong><br>
        {{ t('incident.bikeType') }}: <strong>{{ t(utils.getBikeType(incident)) }}</strong><br>

        <div v-if="incident.childCheckbox === true">
            <i class="fa fa-caret-right"></i> {{ t('incident.childInvolved') }}
        </div>
        <div v-if="incident.trailerCheckbox === true">
            <i class="fa fa-caret-right"></i> {{ t('incident.bikeWithTrailer') }}
        </div>

        <p v-if="incident.desc !== null">{{ incident.desc }}</p>

        <template v-if="isDebug && false">
            <hr>Debug information: {{ incident }}<hr>
        </template>

        <button class="button is-primary is-small is-rounded is-fullwidth" v-if="showRouteEnabled || true" @click="showRoute">{{ t('incident.showBikeRide') }}</button>
    </div>
</template>

<script>
import { IncidentUtils } from "@/services/IncidentUtils";

export default {
    name: "MapPopup",
    props: {
        incident: Object,
        showRouteEnabled: Boolean,
        showRoute: Function,
        t: Function, // Translations don't work in manually mounted components
    },
    data() {
        return {
            utils: IncidentUtils, // this is necessary because mixins don't work with dynamically created components
        };
    },
    methods: {
        isDebug: () => process.env.VUE_APP_DEBUG === "true",
    }
};
</script>

<style scoped lang="scss">
    hr {
        margin: 8px -20px 12px;
    }

    button {
        margin-top: 12px;
    }
</style>
