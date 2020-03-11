FROM adoptopenjdk:13-jre-hotspot

COPY --from=MAVEN /tmp/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java","-jar","/app.jar"]
