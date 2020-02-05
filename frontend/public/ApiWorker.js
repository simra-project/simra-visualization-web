
var queue = [];
var total_to_load = 0;
var progress = 0;

self.onmessage = function(event) {
    switch (event.data[0]) {
        case "routes":
            loadRoutes(event.data[1]);
            break;
        case "matched":
            queue = toTiles(event.data[1]);
            total_to_load = queue.length;
            progress = 0;
            updateLoadingProgress(progress, total_to_load);
            self.postMessage(["queue", queue]);
            loadLegs(event.data[2]);
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

function toTiles(coords) {
    let min_x = Math.min(coords[0][0], coords[1][0]);
    let max_x = Math.max(coords[0][0], coords[1][0]);
    let min_y = Math.min(coords[0][1], coords[1][1]);
    let max_y = Math.max(coords[0][1], coords[1][1]);
    let step = Math.ceil(Math.max((max_x - min_x) / 4, (max_y - min_y) / 4));
    console.log(`step is ${step}`);
    let bounds = 0.05;
    let tiles = [];

    console.log(`min_x: ${min_x}, mod: ${min_x%step}`);

    for (let x = min_x-min_x%step; x<=max_x+(1-max_x%step); x+=step) {
        for (let y = min_y-min_y%step; y<=max_y+(1-max_y%step); y+=step) {
            tiles.push([[x-bounds,y-bounds], [x+step+bounds,y+step+bounds]]);
        }
    }

    return tiles;
}

var routesLoaded = [];
function loadRoutes(data) {
    startLoading();
    let progress = 0;
    let expectedTotal = 0;

    data.forEach(coords => {
        if (!routesLoaded.hasOwnProperty(coords)) {
            expectedTotal++;

            console.log(coords);
            console.log(`http://${ document.location.hostname }:8080/rides/area?bottomleft=${coords[0]/100},${coords[1]/100}&topright=${(coords[0]+1)/100},${(coords[1]+1)/100}`);
            fetch(`http://${ document.location.hostname }:8080/rides/area?bottomleft=${coords[0]/100},${coords[1]/100}&topright=${(coords[0]+1)/100},${(coords[1]+1)/100}`)
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
var loadLegsRunning = false;
async function loadLegs(filter) {
    if (loadLegsRunning)
        return;
    loadLegsRunning = true;
    while (!(queue.length === 0)) {
        coords = queue.pop();
        if (!legsLoaded.hasOwnProperty(coords)) {
            // console.log("starting " + coords);
            // console.log(coords);
            // console.log(`http://${ document.location.hostname }:8080/legs/area?bottomleft=${coords[0][0] / 100},${coords[0][1] / 100}&topright=${coords[1][0] / 100},${coords[1][1] / 100}&minWeight=${filter}`);
            var r = await fetch(`http://${ document.location.hostname }:8080/legs/area?bottomleft=${coords[0][0] / 100},${coords[0][1] / 100}&topright=${coords[1][0] / 100},${coords[1][1] / 100}&minWeight=${filter}`);
            var result = await r.json();
            legsLoaded[coords] = result;
            // console.log("done with " + coords);
            self.postMessage(["matched", result, coords]);
        } else {
            // console.log("cache hit!");
            self.postMessage(["matched", legsLoaded[coords], coords]);
        }
        progress++;
        updateLoadingProgress(progress, total_to_load)
    }
    loadLegsRunning = false;
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
        console.log(`http://${ document.location.hostname }:8080/incidents/filter?bottomleft=${coords[0][0]/100},${coords[0][1]/100}&topright=${coords[1][0]/100},${coords[1][1]/100}&${filtersQuery}`);
        fetch(`http://${ document.location.hostname }:8080/incidents/filter?bottomleft=${coords[0][0]/100},${coords[0][1]/100}&topright=${coords[1][0]/100},${coords[1][1]/100}&${filtersQuery}`)
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
