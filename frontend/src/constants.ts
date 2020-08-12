
let viewModes = {
    NONE: 0,
    RIDES: 1,
    INCIDENTS: 2,
    COMBINED: 3,
    BOX_ANALYSIS: 4,
    TOOLS: 5,
}

export default {
    viewModes: viewModes,
    viewModeHasLegend(viewMode: number) {
        return viewMode === viewModes.RIDES || viewMode === viewModes.INCIDENTS || viewMode === viewModes.COMBINED;
    }
}
