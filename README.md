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
-- frontend
    -- pom.xml
```
MVN command
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