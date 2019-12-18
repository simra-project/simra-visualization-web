let db;
let objectStore;

self.onmessage = function(event) {
    switch (event.data) {
        case "init":
            {
                let req = indexedDB.open("simra", 1);
                req.onupgradeneeded = function (e) {
                    let db = e.target.result;
                    objectStore = db.createObjectStore("incidents", {autoIncrement: true});
                    self.postMessage("Successfully upgraded db");
                };
                req.onsuccess = function (e) {
                    db = req.result;
                };
                req.onerror = function (e) {
                    self.postMessage("error");
                }
            }
            break;
        case "readAll":
            {
                readAll();
            }
            break;

        case "add":
            {
                add();
            }
            break;
    }
};

function readAll() {
    console.log(db);
    let objectStore = db.transaction("incidents").objectStore("incidents");
    let incidents = [];

    objectStore.openCursor().onsuccess = function(event) {
        let cursor = event.target.result;

        if (cursor) {
            incidents.push(cursor.value.rideId);
            cursor.continue();
        } else {
            self.postMessage("Every incident: " + incidents.join(", "));
        }
    };
}

function add() {
    fetch("http://localhost:8080/incidents?lon=" + 1 + "&lat=" + 1 + "&max=" + 100000).then(r => {
        r.json().then(incidents => {
            incidents.forEach(incident => {
                let request = db
                    .transaction(["incidents"], "readwrite")
                    .objectStore("incidents")
                    .add(incident);

                request.onsuccess = function(event) {
                    self.postMessage("Successfully added Incident in db");
                };

                request.onerror = function(event) {
                    self.postMessage("something went wrong here");
                };
            })
        });
    })
}
