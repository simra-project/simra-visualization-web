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

<style lang="scss">
    .hero {
        background-color: #f3f3f3;
    }

    // Import Bulma's core
    @import "~bulma/sass/utilities/_all";

    // Set your colors
    $primary: hsl(217, 71%, 53%);
    $primary-invert: findColorInvert($primary);

    // Setup $colors to use as bulma classes (e.g. 'is-twitter')
    $colors: (
        "white": ($white, $black),
        "black": ($black, $white),
        "light": ($light, $light-invert),
        "dark": ($dark, $dark-invert),
        "primary": ($primary, $primary-invert),
        "info": ($info, $info-invert),
        "success": ($success, $success-invert),
        "warning": ($warning, $warning-invert),
        "danger": ($danger, $danger-invert)
    );

    // Links
    $link: $primary;
    $link-invert: $primary-invert;
    $link-focus-border: $primary;

    // Import Bulma and Buefy styles
    @import "~bulma";
    @import "~buefy/src/scss/buefy";

    .fcp-container {
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
