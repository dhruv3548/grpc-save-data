# Data Store Application

This is GRPC server for saving/updating data received from client to CSV and XML files.

**For Developers**
Steps for running application for interaction with client:

* Build application using `mvn clean install`. Tests can be skipped using mvn clean install -Dmaven.test.skip=true
* Application can be directly run using jar produced in target folder - `java -jar datastore-0.0.1-SNAPSHOT.jar`
* Application can be run as container using below steps:
    * Build the docker image by typing docker build -t data-store .
    * Once the image is created successfully run image using `docker run data-store`
* Once application is started GRPC server would be started and client can interact to save data.