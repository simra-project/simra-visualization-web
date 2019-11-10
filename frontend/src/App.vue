<template>
    <div id="app">
        <Navigation></Navigation>

        <section class="hero is-link is-fullheight-with-navbar">
            <router-view></router-view>
        </section>
    </div>
</template>

<script>
import Navigation from "@/components/Navigation";

export default {
    components: { Navigation },
};
</script>
