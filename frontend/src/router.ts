import Vue from "vue";
import VueRouter from "vue-router";
import Map from "@/views/Map.vue";
import Statistics from "@/views/Statistics.vue";
import About from "@/views/About.vue";

Vue.use(VueRouter);

const routes = [
    {
        path: "/",
        redirect: "/map",
    },
    {
        path: "/map",
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
];

const router = new VueRouter({
    mode: "history",
    linkExactActiveClass: "is-active",
    base: process.env.BASE_URL,
    routes,
});

export default router;
