FROM maven:3-jdk-13 AS MAVEN

# The POM itself for maven
COPY pom.xml /tmp/
# The sourcecode obviously
COPY src /tmp/src/
# The frontend css styles of this vaadin application
COPY frontend /tmp/frontend/

# The credentials for the github package registry to download etgramlis package
COPY settings.xml /tmp/

WORKDIR /tmp/

RUN mvn com.github.eirslett:frontend-maven-plugin:1.9.1:install-node-and-npm -DnodeVersion="v12.16.1"
# build it
RUN mvn package -P production -s settings.xml
# ---
FROM adoptopenjdk:13-jre-hotspot

COPY --from=MAVEN /tmp/target/*.jar app.jar
# Meh, hopefully we don't need the stringtemplate in the future anymore
COPY src/main/resources/ebnf.stg src/main/resources/ebnf.stg

EXPOSE 8080

ENTRYPOINT ["java","-jar","/app.jar"]
