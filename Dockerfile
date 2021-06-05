FROM openjdk:11-jdk-slim

COPY target/*.jar /app.jar

CMD ["java", "-jar", "/app.jar"]