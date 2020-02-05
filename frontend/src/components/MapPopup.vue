<template>
    <div>
        <strong v-if="incident.scary">Scary</strong> Incident on {{ utils.getDate(incident) }}<br>
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

        <button class="button is-primary is-small is-fullwidth" v-if="viewMode !== 2" @click="showRoute">Show Bike Ride</button>
    </div>
</template>

<script>
import { IncidentUtils } from "@/services/IncidentUtils";

export default {
    name: "MapPopup",
    props: {
        viewMode: Number,
        incident: Object,
        showRoute: Function,
    },
    data() {
        return {
            utils: IncidentUtils, // this is necessary because mixins don't work with dynamically created components
        };
    },
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
