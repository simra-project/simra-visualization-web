<template>
    <nav class="container navbar" role="navigation" aria-label="main navigation">
        <div class="navbar-brand">
            <router-link to="/" class="navbar-item">
                <img src="../assets/Logo_MCC.png" height="28">
            </router-link>

            <a role="button" class="navbar-burger burger" aria-label="menu" aria-expanded="false" data-target="navbarBasicExample">
                <span aria-hidden="true"></span>
                <span aria-hidden="true"></span>
                <span aria-hidden="true"></span>
            </a>
        </div>

        <div id="navbarBasicExample" class="navbar-menu">
            <div class="navbar-start">
                <router-link to="/map" class="navbar-item">Map</router-link>
                <router-link to="/statistics" class="navbar-item">Statistics</router-link>
                <router-link to="/about" class="navbar-item">About</router-link>
            </div>

            <div class="navbar-end">
                <div class="navbar-item">
                    <div class="buttons">
                        <a class="button is-primary" href="https://www.mcc.tu-berlin.de/menue/forschung/projekte/simra/">
                            <strong>Download App</strong>
                        </a>
                    </div>
                </div>
            </div>
        </div>
    </nav>
</template>

<script>
export default {
    name: "Navigation",
};
</script>

<style scoped lang="scss">
    .navbar-item {
        &.is-active {
            background-color: #f4f4f4 !important;
        }

        img {
            max-height: 2.25rem;
        }
    }
</style>