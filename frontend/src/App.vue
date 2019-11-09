<template>
    <div id="app">
        <Navigation :locations="locations"></Navigation>

        <section class="hero is-fullheight-with-navbar">
            <router-view></router-view>
        </section>
    </div>
</template>

<script>
import Navigation from "@/components/Navigation";

export default {
    components: { Navigation },
    data() {
        return {
            locations: [ // TODO: echte Positionen herausfinden
                { name: "Berlin", lat: 52.94838, lng: 20.47383, zoom: 7 },
                { name: "Bern", lat: 38.94838, lng: 120.47383, zoom: 10 },
            ],
        };
    },
};
</script>

<style lang="scss" scoped>
    .hero {
        background-color: #f3f3f3;
    }

</style>
