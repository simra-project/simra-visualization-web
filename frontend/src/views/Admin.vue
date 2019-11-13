<template>
    <div>
        <div class="hero-body" v-if="!signedIn">
            <div class="container">
                <div class="columns">
                    <div class="column"></div>
                    <div class="column is-one-third login-container">
                        <b-message class="is-danger" v-if="errorMessage !== null">{{ errorMessage }}</b-message>

                        <form @submit="login">
                            <b-field label="Enter Password">
                                <b-input type="password" v-model="password" @keyup.enter="login" required></b-input>
                            </b-field>

                            <button class="button is-primary is-fullwidth">Login</button>
                        </form>
                    </div>
                    <div class="column"></div>
                </div>
            </div>
        </div>
        <div class="container content admin-container" v-if="signedIn">
            <h2>Admin Tools (secret area)</h2>

            <div class="wrapper">
                <h3>Actions</h3>
                <b-button type="is-primary" outlined icon-left="play"
                          @click="startDataProcessing" :loading="isLoadingDataProcessing">
                    Start Data processing
                </b-button>

                <hr>

                <h3>Locations</h3>
                <ul>
                    <li v-for="location in locations">
                        {{ location.name }}
                        <ul>
                            <li>Lat: {{ location.lat }}</li>
                            <li>Long: {{ location.lng }}</li>
                            <li>Zoom: {{ location.zoom }}</li>
                        </ul>
                    </li>
                </ul>
            </div>
        </div>
    </div>
</template>

<script>
import { ApiService } from "@/services/ApiService";

export default {
    name: "admin",
    components: {},
    props: {
        locations: Array,
    },
    data() {
        return {
            signedIn: false,
            errorMessage: null,
            password: null,
            isLoadingDataProcessing: false,
        };
    },
    methods: {
        login: function (e) {
            e.preventDefault();
            this.errorMessage = null;

            // TODO: this is baaad
            if (this.password === "fcp19") {
                this.signedIn = true;
            } else {
                this.errorMessage = "Wrong password";
            }
        },
        startDataProcessing: function (e) {
            this.isLoadingDataProcessing = true;

            setTimeout(() => {
                ApiService.startDataProcessing();
                this.isLoadingDataProcessing = false;
            }, 1500);
        },
    },
};

</script>

<style lang="scss" scoped>
    .login-container {
        background-color: white;
        border: 1px solid #ddd;
        text-align: center;
    }

    .admin-container {
        h2 {
            margin-top: 30px;
        }

        & > .wrapper {
            background-color: white;
            border: 1px solid #ddd;
            padding: 16px 20px;

            hr + h3 {
                margin-top: 0;
            }
        }
    }
</style>
