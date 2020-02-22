FROM maven:3.5.2-jdk-8-alpine AS MAVEN_BUILD
MAINTAINER Adelaida Alonso
COPY pom.xml /build/
COPY src /build/src/
WORKDIR /build/
RUN mvn test
RUN mvn package
FROM openjdk:8-jre-alpine
WORKDIR /app
COPY --from=MAVEN_BUILD /build/target/accountapi-0.0.1-SNAPSHOT.jar /app/accountapi.jar
ENTRYPOINT ["java", "-jar", "accountapi.jar"]