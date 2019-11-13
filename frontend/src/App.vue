<template>
    <div id="app">
        <Navigation :locations="locations"></Navigation>

        <section class="hero is-fullheight-with-navbar">
            <router-view :locations="locations"></router-view>
        </section>
    </div>
</template>

<script>
import Navigation from "@/components/Navigation";

export default {
    components: { Navigation },
    data() {
        return {
            locations: [
                { name: "Berlin", lat: 52.506818, lng: 13.40229, zoom: 11 },
                { name: "Bern", lat: 46.944696, lng: 7.447443, zoom: 13 },
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
