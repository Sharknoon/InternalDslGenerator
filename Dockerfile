FROM maven:3-jdk-11 AS MAVEN

COPY pom.xml /tmp/
# Needed to download from the github package registry
COPY settings.xml /tmp/
COPY src /tmp/src/
COPY frontend /tmp/frontend/

WORKDIR /tmp/

RUN mvn com.github.eirslett:frontend-maven-plugin:1.9.1:install-node-and-npm -DnodeVersion="v12.16.1"
RUN mvn package -P production -s settings.xml


FROM openjdk:11-jre-slim

COPY --from=MAVEN /tmp/target/*.jar app.jar
# Meh, hopefully we don't need the stringtemplate in the future anymore
COPY src/main/resources/ebnf.stg src/main/resources/ebnf.stg

EXPOSE 8080

ENTRYPOINT ["java","-jar","/app.jar"]
