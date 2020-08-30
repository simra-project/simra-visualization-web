<template>
    <b-navbar style="border-bottom: 1px solid #ddd">
        <template slot="brand">
            <b-navbar-item tag="router-link" :to="{ path: '/' }">
                <img src="../assets/Logo_MCC.png" height="28">
                <span class="nav-brand-text">SimRa Visualization</span>
            </b-navbar-item>
        </template>
        <template slot="start">
<!--            <router-link to="/map" class="navbar-item" :class="{'is-active': this.$route.path.startsWith('/map')}">Map</router-link>-->

<!--            <b-navbar-dropdown :hoverable="true" :class="{'dropdown-active': this.$route.path.startsWith('/map')}" boxed>-->
<!--                <template v-slot:label>-->
<!--                    <router-link to="/map" tag="span">Map</router-link>-->
<!--                </template>-->

<!--                <router-link-->
<!--                    v-for="location in locations"-->
<!--                    :to="{ name: 'mapQuery', params: { lat:  location.lat, lng: location.lng, zoom: location.zoom } }"-->
<!--                    v-slot="{ href }">-->
<!--                    <b-navbar-item :href="href"><span class="icon"><i class="fas fa-city"></i></span> {{ location.name }}</b-navbar-item>-->
<!--                </router-link>-->
<!--            </b-navbar-dropdown>-->

<!--            <router-link to="/statistics" class="navbar-item">Statistics</router-link>-->
<!--            <router-link to="/about" class="navbar-item">About</router-link>-->
        </template>

        <template slot="end">
            <b-navbar-dropdown :label="$t('navigation.mapStyle', [mapStyle.name])" ref="mapStyleDropdown">
                <div class="map-style-container">
                    <div v-for="style in config.mapStyles"
                         class="map-style"
                         :class="{'selected': style.key === mapStyle.key}"
                         :key="style.key"
                         @click="switchToMapStyle(style)"
                    >
                        <div class="img" :style="'background-image: url(\'' + previewURL(style.url) + '\')'"></div>
                        <span>{{ style.name + (style.key === mapStyle.key ? $t('navigation.selected') : '')}}</span>
                    </div>
                </div>
            </b-navbar-dropdown>

            <b-navbar-dropdown class="lang-switcher">
                <template v-slot:label>
                    <country-flag v-if="$i18n.locale === 'en'" country='gb'/>
                    <country-flag v-if="$i18n.locale === 'de'" country='de'/>
                </template>

                <b-navbar-item v-for="(lang, langId) in languages"
                               @click="switchLanguage(langId)">
                    <country-flag :country='lang.flagCode'/> {{ $t(lang.name) }}
                </b-navbar-item>
            </b-navbar-dropdown>

            <div class="navbar-item">
                <div class="buttons">
                    <a class="button is-primary" href="https://www.mcc.tu-berlin.de/menue/forschung/projekte/simra/">
                        <strong>{{ $t('navigation.downloadApp') }}</strong>
                    </a>
                </div>
            </div>
        </template>
    </b-navbar>
</template>

<script>
import VueCookie from 'vue-cookie';
import CountryFlag from 'vue-country-flag';
import Config from "@/constants";

export default {
    name: "Navigation",
    components: { CountryFlag },
    props: {
        mapStyle: { default: {} },
        center: { default: {} },
        zoom: { default: {} },
    },
    data() {
        return {
            config: Config,
            languages: {
                'en': {
                    name: 'navigation.lang.english',
                    flagCode: 'gb',
                },
                'de': {
                    name: 'navigation.lang.german',
                    flagCode: 'de',
                },
            },
        }
    },
    methods: {
        previewURL(url) {
            // Converting center coordinate to nearest tile
            // Found here: https://stackoverflow.com/a/23058284/410143
            const xtile = parseInt(Math.floor((this.center.lng + 180) / 360 * (1 << this.zoom)));
            const latRadians = this.center.lat * Math.PI / 180;
            const ytile = parseInt(Math.floor((1 - Math.log(Math.tan(latRadians) + 1 / Math.cos(latRadians)) / Math.PI) / 2 * (1 << this.zoom)));

            url = url.replace("{z}", this.zoom);
            url = url.replace("{x}", xtile);
            url = url.replace("{y}", ytile);
            url = url.replace("{r}", '');
            url = url.replace("{s}", 'a');
            return url;
        },
        switchToMapStyle(mapStyle) {
            this.$refs.mapStyleDropdown.closeMenu();
            this.$emit('update:map-style', mapStyle);
        },
        switchLanguage(lang) {
            if (lang === this.$i18n.locale) return;

            this.$i18n.locale = lang;
            VueCookie.set('locale', lang, 365 * 100);
        },
    }
};
</script>

<style scoped lang="scss">
    nav.navbar {
        z-index: 500;
    }

    .navbar-item {
        &.is-active,
        &.dropdown-active {
            background-color: #f4f4f4 !important;
        }

        img {
            max-height: 2.25rem;
        }
    }

    .navbar-dropdown .navbar-item .icon {
        margin-right: 4px;
    }

    .navbar-brand {
        padding: 0 8px;

        .nav-brand-text {
            padding-left: 8px;
            color: #ff0102;
            font-size: 1.25rem;
            font-weight: bold;
            font-family: BlinkMacSystemFont, -apple-system, "Segoe UI", Roboto, Oxygen, Ubuntu, Cantarell, "Fira Sans", "Droid Sans", "Helvetica Neue", Helvetica, Arial, sans-serif;
        }
    }

    .navbar-item.has-dropdown .map-style .img {
        display: none;
    }

    .navbar-item.has-dropdown.is-active .map-style .img {
        display: block;
    }

    .map-style {
        height: 120px;
        cursor: pointer;

        .img {
            width: 100%;
            height: 120px;
            max-height: 120px;
            object-fit: cover;
        }

        span {
            position: relative;
            background-color: rgba(33, 33, 33, 0.5);
            color: white;
            bottom: 21px;
            display: block;
            padding-left: 8px;
            width: 100%;
            margin-bottom: -21px;
        }

        &.selected {
            cursor: auto;

            .img {
                filter: contrast(0.5);
            }
        }
    }
</style>

<style lang="scss">
    .navbar-item.has-dropdown .navbar-dropdown {
        padding-top: 0 !important;
        padding-bottom: 0 !important;
        overflow-y: scroll;
        max-height: calc(100vh - 52px - 22px - 52px);
    }

    .lang-switcher  {
        .navbar-link {
            padding-right: 1.5em !important;
            padding-left: 0;

            .flag {
                filter: grayscale(0.25);
                transition: filter 250ms ease;
            }
        }

        &.is-active .flag, .navbar-link:hover .flag {
            filter: none;
        }
    }
</style>
