# Dockerfile for dataservice
FROM openjdk:8

ARG JAR_FILE=target/dataservice-0.0.1-SNAPSHOT.jar

WORKDIR /opt/app

EXPOSE 8080 3001

COPY ${JAR_FILE} app.jar

ENTRYPOINT ["java","-jar","app.jar"]