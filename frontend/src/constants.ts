
let viewModes = {
    NONE: 0,
    RIDES: 1,
    INCIDENTS: 2,
    COMBINED: 3,
    BOX_ANALYSIS: 4,
    TOOLS: 5,
}

let subViewModes = {
    DEFAULT: 0,
    RIDES_DENSITY: 0,
    RIDES_ORIGINAL: 1,
}

let mapStyles = {
    STADIA_ALIDADE_SMOOTH: {
        key: "STADIA_ALIDADE_SMOOTH",
        name: "Light",
        url: "https://tiles.stadiamaps.com/tiles/alidade_smooth/{z}/{x}/{y}{r}.png",
    },
    STADIA_OUTDOORS: {
        key: "STADIA_OUTDOORS",
        name: "Outdoors",
        url: "https://tiles.stadiamaps.com/tiles/outdoors/{z}/{x}/{y}{r}.png",
    },
    STADIA_ALIDADE_SMOOTH_DARK: {
        key: "STADIA_ALIDADE_SMOOTH_DARK",
        name: "Dark",
        url: "https://tiles.stadiamaps.com/tiles/alidade_smooth_dark/{z}/{x}/{y}{r}.png",
    },
    OSM: {
        key: "OSM",
        name: "OSM Default",
        url: "http://{s}.tile.osm.org/{z}/{x}/{y}.png",
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
