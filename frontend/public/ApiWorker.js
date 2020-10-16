var queue = [];
var total_to_load = 0;
var progress = 0;
var URL_BACKEND = "";

self.onmessage = function (event) {
    switch (event.data[0]) {
        case "backendUrl":
            URL_BACKEND = event.data[1];
            break;
        case "routes":
            loadRoutes(event.data[1]);
            break;
        case "matched":
            queue = toTiles(event.data[1], event.data[2], event.data[3]);
            total_to_load = queue.length;
            progress = 0;
            updateLoadingProgress(progress, total_to_load);
            self.postMessage(["queue", queue]);
            loadLegs();
            break;
        case "incidents":
            loadIncidents(event.data[1], event.data[2]);
            break;
        case "route":
            break;
        case "polygon":
            loadPolygon(event.data[1], event.data[2]);
            break;
    }
};


function spiral(array) {

    // let array = [];
    // for (let i=0; i<matrix.length; i++) {
    //     array.push([]);
    //     for (let j=0; j<matrix[0].length; j++) {
    //         array[array.length - 1].push(JSON)
    //     }
    // }
    const result = [];

    let size_x = array.length;
    let size_y = array[0].length;

    let start_row = Math.floor(size_x / 2);
    let end_row = start_row;
    let start_col = Math.floor(size_y / 2);
    let end_col = start_col - 1;

    let direction = 1;
    let dimension = 0;

    let row = start_row;
    let col = start_col;

    while (result.length !== size_x * size_y) {
        if (row < size_x && col < size_y && row >= 0 && col >= 0) {
            result.push(array[row][col]);
        }
        if (dimension === 0) {
            if (row === end_row) {
                dimension = 1;
                let tmp = start_row;
                start_row = end_row;
                end_row = tmp - direction;
                direction = -direction;
            }
        } else {
            if (col === end_col) {
                dimension = 0;
                let tmp = start_col;
                start_col = end_col;
                end_col = tmp - direction;
            }
        }
        if (dimension === 0) {
            row += direction;
        } else {
            col += direction;
        }
    }

    return result;
}


const updateLoadingProgress = (progress, expectedTotal) => self.postMessage(["progress", progress, expectedTotal]);
const startLoading = () => updateLoadingProgress(0, 1);
const finishLoading = () => updateLoadingProgress(1, 1);

function toTiles(coords, resolution, filters) {
    let min_x = Math.min(coords[0][0], coords[1][0]);
    let max_x = Math.max(coords[0][0], coords[1][0]);
    let min_y = Math.min(coords[0][1], coords[1][1]);
    let max_y = Math.max(coords[0][1], coords[1][1]);
    let N_STEPS = 3;
    let step = Math.ceil(Math.max((max_x - min_x) / N_STEPS, (max_y - min_y) / N_STEPS));
    console.log(`step is ${step}`);
    let bounds = 0.05;
    let tiles = [];
    console.log(`filters: ${filters}`);
    let filterElements = filters != null ? Object.entries(filters).filter(x => x[1] != null) : null;
    let filtersQuery = filterElements != null ? filterElements.map(x => `${ x[0] }=${ x[1] }`).join("&") : "";
    console.log(`filtersquery: ${filtersQuery}`);

    console.log(`min_x: ${min_x}, mod: ${min_x % step}`);

    for (let x = min_x - min_x % step; x <= max_x + (1 - max_x % step); x += step) {
        tiles.push([]);
        for (let y = min_y - min_y % step; y <= max_y + (1 - max_y % step); y += step) {
            tiles[tiles.length - 1].push([[x - bounds, y - bounds], [x + step + bounds, y + step + bounds], resolution, filtersQuery]);
        }
    }

    return spiral(tiles).reverse();
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
            console.log(`${URL_BACKEND}/rides/area?bottomleft=${coords[0] / 100},${coords[1] / 100}&topright=${(coords[0] + 1) / 100},${(coords[1] + 1) / 100}`);
            fetch(`${URL_BACKEND}/rides/area?bottomleft=${coords[0] / 100},${coords[1] / 100}&topright=${(coords[0] + 1) / 100},${(coords[1] + 1) / 100}`)
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
async function loadLegs() {
    if (loadLegsRunning)
        return;
    loadLegsRunning = true;
    while (!(queue.length === 0)) {
        coords = queue.pop();
        let resolution = coords[2];
        let filters = coords[3];
        let identifier = resolution.toString() + "//" + filters.toString();
        if (!legsLoaded.hasOwnProperty(identifier)) {
            legsLoaded[identifier] = [];
        }
        if (!legsLoaded[identifier].hasOwnProperty(coords)) {
            // console.log("starting " + coords);
            // console.log(coords);
            console.log(`${URL_BACKEND}/legs/area?bottomleft=${coords[0][0] / 100},${coords[0][1] / 100}&topright=${coords[1][0] / 100},${coords[1][1] / 100}&minWeight=${resolution}&${filters}`);
            var r = await fetch(`${URL_BACKEND}/legs/area?bottomleft=${coords[0][0] / 100},${coords[0][1] / 100}&topright=${coords[1][0] / 100},${coords[1][1] / 100}&minWeight=${resolution}&${filters}`);
            var result = await r.json();
            legsLoaded[identifier][coords] = result;
            // console.log("done with " + coords);
            self.postMessage(["matched", result, coords]);
        } else {
            // console.log("cache hit!");
            self.postMessage(["matched", legsLoaded[identifier][coords], coords]);
        }
        progress++;
        updateLoadingProgress(progress, total_to_load)
    }
    loadLegsRunning = false;
}

var incidents = {};
function loadIncidents(coords, filter) {
    let filterElements = Object.entries(filter).filter(x => x[1] != null);
    let filter_string = JSON.stringify(filter);

    if (!incidents.hasOwnProperty(filter_string)) {
        incidents[filter_string] = [];
    }

    if (!incidents[filter_string].hasOwnProperty(coords)) {
        startLoading();

        let filtersQuery = filterElements.map(x => `${ x[0] }=${ x[1] }`).join("&");

        console.log(coords);
        console.log(`${URL_BACKEND}/incidents/filter?bottomleft=${coords[0][0] / 100},${coords[0][1] / 100}&topright=${coords[1][0] / 100},${coords[1][1] / 100}&${filtersQuery}`);
        fetch(`${URL_BACKEND}/incidents/filter?bottomleft=${coords[0][0] / 100},${coords[0][1] / 100}&topright=${coords[1][0] / 100},${coords[1][1] / 100}&${filtersQuery}`)
            .then(r => r.json())
            .then(result => {
                for (let i = 0; i < result.length; i++) {
                    let seed = parseInt(result[i].properties.key + result[i].properties.ts);
                    result[i].geometry.coordinates[0] += getCoordinateOffset(seed);
                    result[i].geometry.coordinates[1] += getCoordinateOffset(seed + 1);
                }

                incidents[filter_string][coords] = result;
                self.postMessage(["incidents", result]);
                finishLoading();
            });
    } else {
        console.log("cache hit!");
        self.postMessage(["incidents", incidents[filter_string][coords]]);
    }
}

function loadPolygon(polygon, mode) {
    startLoading();
    let url = `http://207.180.205.80:8000/api/rides?${mode}=POLYGON%28%28${polygon.join('%2C')}%29%29&format=json`;
    console.log(url);
    fetch(url).then(r => r.json()).then(result => {
        self.postMessage(["polygon", result]);
        finishLoading();
    });
}

/**
 * This will provide a fixed-per-seed noise offset for one coordinate axis.
 * The value distribution is not uniform (which isn't really needed) but the function performs fast.
 */
function getCoordinateOffset(seed) {
    var x = Math.sin(seed) * 10000;
    x -= Math.floor(x);
    x = (x - 0.5) * 2; // range [-1, 1]

    return x / 5000;
}
