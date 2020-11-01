
let viewModes = {
    NONE: 0,
    RIDES: 1,
    INCIDENTS: 2,
    COMBINED: 3,
    SURFACE_QUALITY: 4,
    RELATIVE_SPEED: 5,
    STOP_TIMES: 6,
    BOX_ANALYSIS: 7,
    TOOLS: 8,
}

let subViewModes = {
    DEFAULT: 0,
    RIDES_DENSITY_ALL: 0,
    RIDES_DENSITY_RUSHHOURMORNING: 2,
    RIDES_DENSITY_RUSHHOUREVENING: 3,
    RIDES_DENSITY_WEEKEND: 4,
    RIDES_ORIGINAL: 1,
    SURFACE_QUALITY_AGGREGATED: 0,
    SURFACE_QUALITY_SINGLE: 1,
    BOX_ANALYSIS_START: 0,
    BOX_ANALYSIS_CONTAINS: 1,
    BOX_ANALYSIS_STOP: 2,
}

let osmAttribution = " &copy; <a href=\"https://www.openstreetmap.org/copyright\">OpenStreetMap</a> contributors";

let mapStyles = {
    CARTO_POSITRON: {
        key: "CARTO_POSITRON",
        name: "Carto Light",
        hasLabelLayer: true,
        url: "https://{s}.basemaps.cartocdn.com/rastertiles/light_nolabels/{z}/{x}/{y}{r}.png",
        urlLabels: "https://{s}.basemaps.cartocdn.com/rastertiles/light_only_labels/{z}/{x}/{y}{r}.png",
        attribution: "&copy; <a href=\"https://carto.com/attributions\">CARTO</a>" + osmAttribution,
    },
    CARTO_VOYAGER: {
        key: "CARTO_VOYAGER",
        name: "Carto Default",
        hasLabelLayer: true,
        url: "https://{s}.basemaps.cartocdn.com/rastertiles/voyager_nolabels/{z}/{x}/{y}{r}.png",
        urlLabels: "https://{s}.basemaps.cartocdn.com/rastertiles/voyager_only_labels/{z}/{x}/{y}{r}.png",
        attribution: "&copy; <a href=\"https://carto.com/attributions\">CARTO</a>" + osmAttribution,
    },
    CARTO_DARK_MATTER: {
        key: "CARTO_DARK_MATTER",
        name: "Carto Dark",
        hasLabelLayer: true,
        url: "https://{s}.basemaps.cartocdn.com/rastertiles/dark_nolabels/{z}/{x}/{y}{r}.png",
        urlLabels: "https://{s}.basemaps.cartocdn.com/rastertiles/dark_only_labels/{z}/{x}/{y}{r}.png",
        attribution: "&copy; <a href=\"https://carto.com/attributions\">CARTO</a>" + osmAttribution,
    },
    OSM: {
        key: "OSM",
        name: "OpenStreetMaps",
        hasLabelLayer: false,
        url: "http://{s}.tile.osm.org/{z}/{x}/{y}.png",
        attribution: "&copy; <a href=\"https://www.openstreetmap.org/copyright\">OpenStreetMap</a> contributors",
    },
    WIKIPEDIA: {
        key: "WIKIPEDIA",
        name: "Wikipedia",
        hasLabelLayer: false,
        url: "https://maps.wikimedia.org/osm-intl/{z}/{x}/{y}.png",
        attribution: "&copy; <a href=\"https://wikimediafoundation.org/wiki/Maps_Terms_of_Use\">Wikimedia</a>" + osmAttribution
    },
    STADIA_ALIDADE_SMOOTH: {
        key: "STADIA_ALIDADE_SMOOTH",
        name: "Stadia Light",
        hasLabelLayer: false,
        url: "https://tiles.stadiamaps.com/tiles/alidade_smooth/{z}/{x}/{y}{r}.png",
        attribution: "&copy; <a href=\"https://stadiamaps.com/\">Stadia Maps</a>, &copy; <a href=\"https://openmaptiles.org/\">OpenMapTiles</a>" + osmAttribution,
    },
    STADIA_OUTDOORS: {
        key: "STADIA_OUTDOORS",
        name: "Stadia Default",
        hasLabelLayer: false,
        url: "https://tiles.stadiamaps.com/tiles/outdoors/{z}/{x}/{y}{r}.png",
        attribution: "&copy; <a href=\"https://stadiamaps.com/\">Stadia Maps</a>, &copy; <a href=\"https://openmaptiles.org/\">OpenMapTiles</a>" + osmAttribution,
    },
    STADIA_ALIDADE_SMOOTH_DARK: {
        key: "STADIA_ALIDADE_SMOOTH_DARK",
        name: "Stadia Dark",
        hasLabelLayer: false,
        url: "https://tiles.stadiamaps.com/tiles/alidade_smooth_dark/{z}/{x}/{y}{r}.png",
        attribution: "&copy; <a href=\"https://stadiamaps.com/\">Stadia Maps</a>, &copy; <a href=\"https://openmaptiles.org/\">OpenMapTiles</a>" + osmAttribution,
    },
}

export default {
    viewModes: viewModes,
    subViewModes: subViewModes,
    viewModeHasLegend(viewMode: number) {
        return viewMode === viewModes.RIDES || viewMode === viewModes.INCIDENTS || viewMode === viewModes.COMBINED;
    },
    mapStyles: mapStyles,
}
