FROM eclipse-temurin:17-jre

WORKDIR /app

COPY target/product-service-1.0.0.jar app.jar

EXPOSE 8081
EXPOSE 5006

ENTRYPOINT ["java", "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5006", "-jar", "app.jar"]
