
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

var routesLoaded = [];
function loadRoutes(data) {
    data.forEach(coords => {
        if (!routesLoaded.hasOwnProperty(coords)) {
            console.log(coords);
            console.log(`http://localhost:8080/rides/area?bottomleft=${coords[0]/100},${coords[1]/100}&topright=${(coords[0]+1)/100},${(coords[1]+1)/100}`);
            fetch(`http://localhost:8080/rides/area?bottomleft=${coords[0]/100},${coords[1]/100}&topright=${(coords[0]+1)/100},${(coords[1]+1)/100}`)
                .then(r => r.json())
                .then(result => {
                    routesLoaded[coords] = result;
                    self.postMessage(["routes", result]);
                });
        }
    })
}

var legsLoaded = [];
function loadLegs(coords, filter) {
    if (!legsLoaded.hasOwnProperty(coords)) {
        console.log(coords);
        console.log(`http://localhost:8080/legs/area?bottomleft=${coords[0][0]/100},${coords[0][1]/100}&topright=${coords[1][0]/100},${coords[1][1]/100}&minWeight=${filter}`);
        fetch(`http://localhost:8080/legs/area?bottomleft=${coords[0][0]/100},${coords[0][1]/100}&topright=${coords[1][0]/100},${coords[1][1]/100}&minWeight=${filter}`)
            .then(r => r.json())
            .then(result => {
                legsLoaded[coords] = result;
                self.postMessage(["matched", result]);
            });
    } else {
        console.log("cache hit!");
        self.postMessage(["matched", legsLoaded[coords]]);
    }
}

var incidents = [];
function loadIncidents(coords, filter) {
    if (!incidents.hasOwnProperty(coords)) {
        console.log(coords);
        console.log(`http://localhost:8080/incidents/area?bottomleft=${coords[0][0]/100},${coords[0][1]/100}&topright=${coords[1][0]/100},${coords[1][1]/100}`);
        fetch(`http://localhost:8080/incidents/area?bottomleft=${coords[0][0]/100},${coords[0][1]/100}&topright=${coords[1][0]/100},${coords[1][1]/100}`)
            .then(r => r.json())
            .then(result => {
                incidents[coords] = result;
                self.postMessage(["incidents", result]);
            });
    } else {
        console.log("cache hit!");
        self.postMessage(["incidents", incidents[coords]]);
    }
}
