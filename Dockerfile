FROM maven:3-jdk-11 AS MAVEN

COPY pom.xml /tmp/
# Needed to download from the github package registry
COPY settings.xml /tmp/
COPY src /tmp/src/

WORKDIR /tmp/

RUN mvn package -P production -s settings.xml


FROM openjdk:11

COPY --from=MAVEN /tmp/target/*.jar app.jar
# Meh, hopefully we don't need the stringtemplate in the future anymore
COPY target/src/main/resources/ebnf.stg src/main/resources/ebnf.stg

EXPOSE 8080

ENTRYPOINT ["java","-jar","/app.jar"]
