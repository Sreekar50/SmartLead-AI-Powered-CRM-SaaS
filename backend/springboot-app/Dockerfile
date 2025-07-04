# backend/springboot-app/Dockerfile
FROM openjdk:17-jdk-alpine

WORKDIR /app

COPY target/smartlead-crm-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]