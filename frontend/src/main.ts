import Vue from "vue";
import App from "./App.vue";
import router from "./router";

import { Icon } from "leaflet";
import "leaflet/dist/leaflet.css";

import Buefy from "buefy";
import "buefy/dist/buefy.css";

Vue.config.productionTip = false;

Vue.use(Buefy);

new Vue({
    router,
    render: h => h(App),
}).$mount("#app");

// delete Icon.Default.prototype._getIconUrl;

Icon.Default.mergeOptions({
    iconRetinaUrl: require("leaflet/dist/images/marker-icon-2x.png"),
    iconUrl: require("leaflet/dist/images/marker-icon.png"),
    shadowUrl: require("leaflet/dist/images/marker-shadow.png"),
});
