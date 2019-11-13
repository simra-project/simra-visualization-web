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
port: 27017   `

To import ride and its incidents use following command (change paths)
$ java -jar backend/csvimporter/target/csvimporter-1.0-SNAPSHOT-jar-with-dependencies.jar -t r /Users/developer/Downloads/SimRa_Sample_10_23_19/Berlin/Rides/VM2_-1430356997

To import profile use following command (change paths)
$ java -jar backend/csvimporter/target/csvimporter-1.0-SNAPSHOT-jar-with-dependencies.jar -t p /Users/developer/Downloads/SimRa_Sample_10_23_19/Berlin/Profiles/VM2_1138028561 

```

TODO: Simultaneous import still in progress.