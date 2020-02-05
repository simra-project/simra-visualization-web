
self.onmessage = function(event) {
    switch (event.data[0]) {
        case "routes":
            loadRoutes(event.data[1]);
            break;
        case "matched":
            loadLegs(event.data[1], event.data[2]);
            break;
        case "incidents":
            loadIncidents(event.data[1], event.data[2]);
            break;
        case "route":
            break;
    }
};

const updateLoadingProgress = (progress, expectedTotal) => self.postMessage(["progress", progress, expectedTotal]);
const startLoading = () => updateLoadingProgress(0, 1);
const finishLoading = () => updateLoadingProgress(1, 1);

var routesLoaded = [];
function loadRoutes(data) {
    startLoading();
    let progress = 0;
    let expectedTotal = 0;

    data.forEach(coords => {
        if (!routesLoaded.hasOwnProperty(coords)) {
            expectedTotal++;

            console.log(coords);
            console.log(`http://localhost:8080/rides/area?bottomleft=${coords[0]/100},${coords[1]/100}&topright=${(coords[0]+1)/100},${(coords[1]+1)/100}`);
            fetch(`http://localhost:8080/rides/area?bottomleft=${coords[0]/100},${coords[1]/100}&topright=${(coords[0]+1)/100},${(coords[1]+1)/100}`)
                .then(r => r.json())
                .then(result => {
                    routesLoaded[coords] = result;
                    self.postMessage(["routes", result]);
                    updateLoadingProgress(++progress, expectedTotal);
                });
        }
    })
}

var legsLoaded = [];
function loadLegs(coords, filter) {
    if (!legsLoaded.hasOwnProperty(coords)) {
        startLoading();

        console.log(coords);
        console.log(`http://localhost:8080/legs/area?bottomleft=${coords[0][0]/100},${coords[0][1]/100}&topright=${coords[1][0]/100},${coords[1][1]/100}&minWeight=${filter}`);
        fetch(`http://localhost:8080/legs/area?bottomleft=${coords[0][0]/100},${coords[0][1]/100}&topright=${coords[1][0]/100},${coords[1][1]/100}&minWeight=${filter}`)
            .then(r => r.json())
            .then(result => {
                legsLoaded[coords] = result;
                self.postMessage(["matched", result]);
                finishLoading();
            });
    } else {
        console.log("cache hit!");
        self.postMessage(["matched", legsLoaded[coords]]);
    }
}

var incidents = [];
var prevLoadedWithFilters = false;
function loadIncidents(coords, filter) {
    let filterElements = Object.entries(filter).filter(x => x[1] != null);
    prevLoadedWithFilters = filterElements.length > 0;

    if (!incidents.hasOwnProperty(coords) || filterElements.length > 0 || prevLoadedWithFilters) {
        startLoading();

        let filtersQuery = filterElements.map(x => `${ x[0] }=${ x[1] }`).join("&");

        console.log(coords);
        console.log(`http://localhost:8080/incidents/filter?bottomleft=${coords[0][0]/100},${coords[0][1]/100}&topright=${coords[1][0]/100},${coords[1][1]/100}&${filtersQuery}`);
        fetch(`http://localhost:8080/incidents/filter?bottomleft=${coords[0][0]/100},${coords[0][1]/100}&topright=${coords[1][0]/100},${coords[1][1]/100}&${filtersQuery}`)
            .then(r => r.json())
            .then(result => {
                incidents[coords] = result;
                self.postMessage(["incidents", result]);
                finishLoading();
            });
    } else {
        console.log("cache hit!");
        self.postMessage(["incidents", incidents[coords]]);
    }
}
