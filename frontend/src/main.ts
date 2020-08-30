import Vue from "vue";
// @ts-ignore
import VueWorker from "vue-worker";
import App from "./App.vue";
import router from "./router";
import "leaflet/dist/leaflet.css";

import Buefy from "buefy";
// Fix vue-loader/webpack error (https://github.com/KoRiGaN/Vue2Leaflet/issues/28)
import "leaflet-defaulticon-compatibility/dist/leaflet-defaulticon-compatibility.webpack.css"; // Re-uses images from ~leaflet package
import "leaflet-defaulticon-compatibility";

import i18n from './i18n';

Vue.config.productionTip = false;

Vue.use(Buefy, {
    defaultIconPack: "fas",
});
Vue.use(VueWorker);

new Vue({
    router,
    i18n,
    render: h => h(App),
}).$mount("#app");
