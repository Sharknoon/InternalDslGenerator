# Run mvnw package first
FROM openjdk:11-jdk-alpine
# Adding a User with non root privileges, optional
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/app.jar"]