<template>
    <div>
        <strong v-if="incident.scary">Scary</strong> Incident on {{ utils.getDate(incident) }}<br>
        <hr>

        Type: <strong>{{ utils.getType(incident) }}</strong><br>
        Participant: <strong>{{ utils.getParticipant(incident) }}</strong><br>

        <p v-if="incident.description !== null">{{ incident.description }}</p>

        <button class="button is-primary is-small is-fullwidth" @click="showRoute">Show Bike Ride</button>
    </div>
</template>

<script>
import { IncidentUtils } from "@/services/IncidentUtils";

export default {
    name: "MapPopup",
    props: {
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
