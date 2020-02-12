<template>
    <b-navbar class="container">
        <template slot="brand">
            <b-navbar-item tag="router-link" :to="{ path: '/' }">
                <img src="../assets/Logo_MCC.png" height="28">
            </b-navbar-item>
        </template>
        <template slot="start">
            <router-link to="/map" class="navbar-item" :class="{'is-active': this.$route.path.startsWith('/map')}">Map</router-link>

<!--            <b-navbar-dropdown :hoverable="true" :class="{'dropdown-active': this.$route.path.startsWith('/map')}" boxed>-->
<!--                <template v-slot:label>-->
<!--                    <router-link to="/map" tag="span">Map</router-link>-->
<!--                </template>-->

<!--                <router-link-->
<!--                    v-for="location in locations"-->
<!--                    :to="{ name: 'mapQuery', params: { lat:  location.lat, lng: location.lng, zoom: location.zoom } }"-->
<!--                    v-slot="{ href }">-->
<!--                    <b-navbar-item :href="href"><span class="icon"><i class="fas fa-city"></i></span> {{ location.name }}</b-navbar-item>-->
<!--                </router-link>-->
<!--            </b-navbar-dropdown>-->

            <router-link to="/statistics" class="navbar-item">Statistics</router-link>
            <router-link to="/about" class="navbar-item">About</router-link>
        </template>

        <template slot="end">
            <div class="navbar-item">
                <div class="buttons">
                    <a class="button is-primary" href="https://www.mcc.tu-berlin.de/menue/forschung/projekte/simra/">
                        <strong>Download App</strong>
                    </a>
                </div>
            </div>
        </template>
    </b-navbar>
</template>

<script>
export default {
    name: "Navigation",
    props: {
        locations: Array,
    },
};
</script>

<style scoped lang="scss">
    nav.navbar {
        z-index: 500;
    }

    .navbar-item {
        &.is-active,
        &.dropdown-active {
            background-color: #f4f4f4 !important;
        }

        img {
            max-height: 2.25rem;
        }
    }

    .navbar-dropdown .navbar-item .icon {
        margin-right: 4px;
    }
</style>
