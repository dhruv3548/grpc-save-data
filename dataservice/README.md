# Data Service Application

This is GRPC client application for saving/updating and reading data by interacting with running GRPC server data-store.

**For Developers**

Steps for running application for interaction with server and accessing user APIs:

* Build application using `mvn clean install`. Tests can be skipped using mvn clean install -Dmaven.test.skip=true
* Application can be directly run using jar produced in target folder - `java -jar dataservice-0.0.1-SNAPSHOT.jar`
* Application can be run as container using below steps:
    * Build the docker image by typing docker build -t data-service .
    * Once the image is created successfully run image using `docker run data-service`
* Once application is started refer API document below.

# API

***Save Data***:

- **Method type**: POST
- **URI**: http://localhost:8080/data
- **Request Param**: fileType
    * _Supported values_: CSV or XML
- **Request Body**: 
    * _Sample data_: {
                       "data" : [
                       {
                           "name" : "John",
                           "dob" : "1-03-1991",
                           "salary" : 200000.500,
                           "age" : 31
                       },
                       {
                           "name" : "Katty",
                           "dob" : "17-05-1989",
                           "salary" : 300000.500,
                           "age" : 29
                       },
                       {
                           "name" : "Dhruv",
                           "dob" : "17-04-1990",
                           "salary" : 200000.500,
                           "age" : 30
                       }
                   ]
                   } 
- **Description**: Saves the data records mentioned in payload to specified file type (CSV or XML)  

***Update Data***:
- **Method type**: PUT
- **URI**: http://localhost:8080/data
- **Request Param**: fileType
    * _Supported values_: CSV or XML
- **Request Body**: 
    * _Sample data_: {
                         "data" : [
                         {
                             "name" : "Dhruv",
                             "dob" : "17-04-1992",
                             "salary" : 200000.500,
                             "age" : 29
                         }
                     ]
                     }
- **Description**: Updates the set of records provided in payload in respective file using fileType parameter value
. If records does not exist, it will add one.
     
***Read Data***:
- **Method type**: GET
- **URI**: http://localhost:8080/data
- **Description**: Returns all data from reading and collaborating from both the files.