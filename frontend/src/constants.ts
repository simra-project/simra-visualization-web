
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

export default {
    viewModes: viewModes,
    subViewModes: subViewModes,
    viewModeHasLegend(viewMode: number) {
        return viewMode === viewModes.RIDES || viewMode === viewModes.INCIDENTS || viewMode === viewModes.COMBINED;
    }
}
