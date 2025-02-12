# Use an official Java runtime as a parent image
FROM eclipse-temurin:23-jdk-alpine

# Set the working directory inside the container
WORKDIR /app

# Copy the built JAR file into the container
COPY target/basket-service-1.0.0.jar app.jar

# Expose the application port
EXPOSE 8081

# Expose the debugger port
EXPOSE 5007

# Run the application with remote debugging enabled
CMD ["java", "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5007", "-jar", "app.jar"]
