# FCP-WS19

Repository for the Fog Computing Project (WS2019/2020).

https://www.digital-future.berlin/forschung/projekte/simra/

# Maven structure

```
pom.xml
-- backend
    -- pom.xml
        -- csvimporter
            --pom.xml
        -- SimRa-Visualization-API
            --pom.xml
-- frontend
    -- pom.xml
```
#MVN command

```shell script
To clean:
$ mvn clean

To build complete project:
$ mvn clean install 

To compile complete project
$ mvn compile

To test complete project
$ mvn test

To clean install or compile or test a module
$ mvn clean install -pl :<artifactId>

```

# DB connection
To connect to your mongodb use config.properties file to set up credentials.

```shell script
Default credentials are followings:  
host: localhost  
port: 27017   

To import ride and its incidents use following command (change paths)
$ java -jar backend/csvimporter/target/csvimporter-1.0-SNAPSHOT-jar-with-dependencies.jar -a 12 -e 0.00001 -t r /Users/developer/Downloads/SimRa_Sample_10_23_19/Berlin/Rides/VM2_-1430356997

To import profile use following command (change paths)
$ java -jar backend/csvimporter/target/csvimporter-1.0-SNAPSHOT-jar-with-dependencies.jar -t p /Users/developer/Downloads/SimRa_Sample_10_23_19/Berlin/Profiles/VM2_1138028561 

```
Note: Rides and Profiles can not be imported simultaneously.

| Parameter        |Mandatory     | Default Value | Description |
| ---------------- |:------------:|:------------:|:------------:|
|                  | Yes | - | Path to CSV Data or Directory |
| ```-t``` / ```--type```| Yes | - | For Rides use ```r```, for Profiles use ```p``` |
| ```-p``` / ```--path```| Yes | - | For Single file use ```f```, for Directory use ```d``` |
| ```-a``` / ```--accuracy```| No | 20 | When importing Rides: Minimum acc-value of Coordinates that will pass Accuracy Filter |
| ```-e``` / ```--epsilon``` | No | 0.0000001| When importing Rides: Epsilon value of RDP-Algorithm |




# Database Structure
Info: So far only filtered data is pushed into the database.

- Incidents DB Example (incidentsCollection)
```json
{
    "_id" : {
        "rideId" : "VM2_-1430356997",
        "key" : 0
    },
    "key" : 0,
    "bike" : 1,
    "childCheckBox" : 0,
    "trailerCheckBox" : 0,
    "pLoc" : 5,
    "incident" : 0,
    "i1" : 0,
    "i2" : 0,
    "i3" : 0,
    "i4" : 0,
    "i5" : 0,
    "i6" : 0,
    "i7" : 0,
    "i8" : 0,
    "i9" : 0,
    "i10" : 0,
    "scary" : 0,
    "description" : "",
    "timestamp" : 1567147746586,
    "coordinates" : {
        "type" : "Point",
        "coordinates" : [ 
            52.51170886, 
            13.30756922
        ]
    }
}

```
- Ride DB Example (ridesCollection)
```json
{
    "_id" : "5dd42373b7f089bc429e1fd9",
    "rideId" : "VM2_-1438740659",
    "coordinates" : {
        "type" : "LineString",
        "coordinates" : [ 
            [ 
                1.8196564, 
                13.30653516
            ], 
            [ 
                -0.18409729, 
                13.30653733
            ], 
            [ 
                0.12376404, 
                13.30655848
            ], 
            [ 
                0.08540344, 
                13.30660544
            ], 
            [ 
                0.23667908, 
                13.30666846
            ]
        ]
    },
    "ts" : [ 
          1567147661245, 
          1567147664279, 
          1567147667281, 
          1567147670321, 
          1567147673346
    ]    
}

```

- Profile DB Example (profilesCollection)
```json
{
    "_id" : "5dcfe057b7f0892d3c728bdc",
    "fileId" : "VM2_125791469",
    "appVersion" : "i5",
    "fileVersion" : 13,
    "birth" : 6,
    "gender" : 1,
    "region" : 1,
    "numberOfRides" : 14,
    "duration" : 26849369,
    "numberOfIncidents" : 3,
    "distance" : 141324.0,
    "co2" : 0,
    "idle" : 0,
    "numberOfScary" : 0,
    "behaviour" : 0,
    "hours" : {
        "11" : "1",
        "22" : "0",
        "12" : "1",
        "23" : "0",
        "13" : "0",
        "14" : "0",
        "15" : "0",
        "16" : "2",
        "17" : "6",
        "18" : "0",
        "19" : "2",
        "0" : "0",
        "1" : "0",
        "2" : "0",
        "3" : "0",
        "4" : "0",
        "5" : "0",
        "6" : "0",
        "7" : "8",
        "8" : "0",
        "9" : "3",
        "20" : "2",
        "10" : "3",
        "21" : "0"
    }
}
```
