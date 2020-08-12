<template>
    <div>
        <strong v-if="incident.scary">Scary</strong> Incident<template v-if="utils.isRecentDate(incident)"> on {{ utils.getDate(incident) }}</template><br>
        <hr>

        Type: <strong>{{ utils.getType(incident) }}</strong><br>
        Participant: <strong>{{ utils.getParticipant(incident) }}</strong><br>
        Bike Type: <strong>{{ utils.getBikeType(incident) }}</strong><br>

        <div v-if="incident.child === true">
            <i class="fa fa-caret-right"></i> Child involved
        </div>
        <div v-if="incident.trailer === true">
            <i class="fa fa-caret-right"></i> Bike with trailer
        </div>

        <p v-if="incident.description !== null">{{ incident.description }}</p>

        <template v-if="isDebug && false">
            <hr>Debug information: {{ incident }}<hr>
        </template>

        <button class="button is-primary is-small is-fullwidth" v-if="showRouteEnabled || true" @click="showRoute">Show Bike Ride</button>
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
