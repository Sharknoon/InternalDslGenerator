# Maven Package step
FROM maven:3-jdk-11 AS MAVEN

RUN mvn package

FROM openjdk:11

COPY --from=MAVEN target/*.jar app.jar

ENTRYPOINT ["java","-jar","/app.jar"]