# SimRa-Visualization

This project is part of the SimRa research project which includes the following subprojects:
- [sirma-android](https://github.com/simra-project/simra-android/): The SimRa app for Android.
- [simra-ios](https://github.com/simra-project/simra-ios): The SimRa app for iOS.
- [backend](https://github.com/simra-project/backend): The SimRa backend software.
- [dataset](https://github.com/simra-project/dataset): Result data from the SimRa project.
- [screenshots](https://github.com/simra-project/screenshots): Screenshots of both the iOS and Android app.
- [SimRa-Visualization](https://github.com/simra-project/SimRa-Visualization): Web application for visualizing the dataset.

In this project, we collect – with a strong focus on data protection and privacy – data on such near crashes to identify when and where bicyclists are especially at risk. We also aim to identify the main routes of bicycle traffic in Berlin. To obtain such data, we have developed a smartphone app that uses GPS information to track routes of bicyclists and the built-in acceleration sensors to pre-categorize near crashes. After their trip, users are asked to annotate and upload the collected data, pseudonymized per trip.
For more information see [our website](https://www.digital-future.berlin/en/research/projects/simra/).

# Instructions
## Maven structure

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
## MVN command

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

## DB connection
To connect to your mongodb use application.properties file to set up credentials.

```Application.properties
spring.data.mongodb.database=simra
spring.data.mongodb.port=27017
spring.data.mongodb.host=localhost  

Log file path:
logging.file.name = /Users/developer/IdeaProjects/csvimporter/csvimporter.log

CSV root folder to monitor :
csv.monitor.path=/Users/developer/Downloads/SimRa_Sample_10_23_19

simra.region.default=Berlin

# Other configurations can be found in Application.properties file.

```
## Run as Spring Boot Application
```shell script
$ java -jar backend/csvimporter/target/csvimporter-0.0.1-SNAPSHOT.jar  
```




## Database Structure
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
## Region Import
Region is imported by parsing Folder path of ride or profile file. 
Its important to keep Rides and Profiles under City name.

Reference in Android:
[Link](https://github.com/simra-project/backend/blob/master/src/main/java/tuberlin/mcc/simra/backend/servlets/version10/UploadServlet.java#L114)
```
Structure:
Root Folder Path..\<Region>\(Rides|Profiles)\**.csv

Example:
C:\Users\spandey\Downloads\SimRa_Sample_11_13_19\Berlin\Rides
C:\Users\spandey\Downloads\SimRa_Sample_11_13_19\Berlin\profiles

For test:
C:\Users\spandey\Downloads\SimRa_Sample_11_13_19\Berlin\Demo

All of above will have Berlin as city.

```