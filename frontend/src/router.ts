import Vue from "vue";
import VueRouter from "vue-router";
import Map from "@/views/Map.vue";
import Statistics from "@/views/Statistics.vue";
import About from "@/views/About.vue";
import Admin from "@/views/Admin.vue";

Vue.use(VueRouter);

const routes = [
    {
        path: "/",
        redirect: "/map",
    },
    {
        path: "/map",
        name: 'map',
        component: Map,
    },
    {
        path: "/map?lat=:lat&long=:long&z=:zoom",
        name: 'mapQuery',
        component: Map,
    },
    {
        path: "/statistics",
        component: Statistics,
    },
    {
        path: "/about",
        component: About,
    },
    {
        path: "/admin",
        component: Admin,
    },
];

const router = new VueRouter({
    mode: "history",
    linkExactActiveClass: "is-active",
    base: process.env.BASE_URL,
    routes,
});

export default router;
