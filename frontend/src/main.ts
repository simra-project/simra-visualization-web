import Vue from "vue";
// @ts-ignore
import VueWorker from "vue-worker";
Vue.use(VueWorker);

import Buefy from "buefy";
Vue.use(Buefy, {
    defaultIconPack: "fas",
});

// @ts-ignore
import VueCookie from "vue-cookie";
Vue.use(VueCookie);

import App from "./App.vue";
import router from "./router";
import i18n from "./i18n";

import "leaflet/dist/leaflet.css";
// Fix vue-loader/webpack error (https://github.com/KoRiGaN/Vue2Leaflet/issues/28)
import "leaflet-defaulticon-compatibility/dist/leaflet-defaulticon-compatibility.webpack.css"; // Re-uses images from ~leaflet package
import "leaflet-defaulticon-compatibility";

Vue.config.productionTip = false;

new Vue({
    router,
    i18n,
    render: h => h(App),
}).$mount("#app");
