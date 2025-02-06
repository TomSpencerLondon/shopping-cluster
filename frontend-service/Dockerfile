FROM eclipse-temurin:17.0.5_8-jre-focal

WORKDIR /app

COPY target/*.jar app.jar

EXPOSE 8085
EXPOSE 5005

ENTRYPOINT ["java", "-jar", "app.jar"]
