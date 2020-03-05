# Maven Package step
FROM maven:3-jdk-11 AS MAVEN

COPY frontend frontend
COPY src src
COPY pom.xml pom.xml

RUN mvn package

FROM openjdk:11

COPY --from=MAVEN target/*.jar app.jar

ENTRYPOINT ["java","-jar","/app.jar"]