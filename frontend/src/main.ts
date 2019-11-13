import 'core-js'
import 'core-js/shim'
import '@babel/polyfill'

import Vue from "vue";
import App from "./App.vue";
import router from "./router";

import "leaflet/dist/leaflet.css";

import Buefy from "buefy";
import "buefy/dist/buefy.css";

// Fix vue-loader/webpack error (https://github.com/KoRiGaN/Vue2Leaflet/issues/28)
import "leaflet-defaulticon-compatibility/dist/leaflet-defaulticon-compatibility.webpack.css"; // Re-uses images from ~leaflet package
import "leaflet-defaulticon-compatibility";


Vue.config.productionTip = false;

Vue.use(Buefy);

new Vue({
    router,
    render: h => h(App),
}).$mount("#app");
