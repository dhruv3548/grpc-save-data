# Dockerfile for dataservice
FROM openjdk:8

ARG JAR_FILE=target/datastore-0.0.1-SNAPSHOT.jar

WORKDIR /opt/app

EXPOSE 9000 3001

COPY ${JAR_FILE} app.jar

ENTRYPOINT ["java","-jar","app.jar"]