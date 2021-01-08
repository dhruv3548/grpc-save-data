# Save data Application

**Requirement**: The requirement of our application is to build GPRC Microservices to securely store data in provided file
             format and allow the user to read and update when required. The backend currently supports storing the
             data in CSV and XML file format.

This application consist of 3 modules as mentioned below and needs to be build and run in similar order.
* proto: Protobuf definition for payload
* datastore: GRPC server for saving/reading records to/from file
* dataservice: Client application which exposes APIs to be consumed by user

Build application using:
`mvn clean install`

For detailed instructions on running data-store and data-service please refer individual readMe files.

Test snapshots have been attached with file names POST.png, PUT.png and GET.png

