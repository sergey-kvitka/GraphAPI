FROM maven:3.8.4-jdk-11-slim as build
COPY . /sources
WORKDIR /sources
RUN mvn clean package

FROM openjdk:11-slim
COPY --from=build /sources/target/*.jar /applications/app.jar
WORKDIR /applications
ENTRYPOINT ["java", "-jar", "./app.jar"]