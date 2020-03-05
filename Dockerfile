# Run mvnw package first
FROM openjdk:11
# Adding a User with non root privileges, optional
RUN useradd -ms /bin/bash spring
WORKDIR /home/spring
USER spring:spring

ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/app.jar"]