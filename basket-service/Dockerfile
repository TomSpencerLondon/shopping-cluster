FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

COPY target/*.jar app.jar

EXPOSE 8081
EXPOSE 5006

ENTRYPOINT ["java", "-jar", "app.jar"]
