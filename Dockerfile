FROM openjdk:11

COPY target/*.jar app.jar
COPY target/src/main/resources/ebnf.stg src/main/resources/ebnf.stg

EXPOSE 8080

ENTRYPOINT ["java","-jar","/app.jar"]
