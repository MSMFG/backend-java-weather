# Prerequisites
    
Before you begin, please make sure you have the following setup:

* A suitable IDE (intelliJ/Eclipse etc)
* JDK 17
* the mockService that acts as a dependency for the test (See below)
* the service dependency that you'll be updating in the test (See below)

## Running the mock Service
For the purpose of the test, a mock service has been provided.

To start up the mock service run:
```
 java -jar service/mockservice/mockservice-0.0.1-SNAPSHOT.jar
```

## Running the service used for the test
To start up the service run
```

./gradlew service:bootRun
```
To validate the service is running, go to `localhost:8080/v1/weather/warmest`