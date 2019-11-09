<template>
    <div>
        <div class="hero-body" v-if="!signedIn">
            <div class="container columns">
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
        <div class="container" v-if="signedIn">
            <h2>Admin Tools (secret area)</h2>
        </div>
    </div>
</template>

<script>

export default {
    name: "admin",
    components: {},
    data() {
        return {
            signedIn: false,
            errorMessage: null,
            password: null,
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
    },
};

</script>

<style lang="scss" scoped>
    .login-container {
        background-color: white;
        border: 1px solid #ddd;
        text-align: center;
    }
</style>
