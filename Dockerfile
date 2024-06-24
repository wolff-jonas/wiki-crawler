# Use an official OpenJDK runtime as a parent image
FROM eclipse-temurin:21

# Set the working directory in the container
WORKDIR /app

# Copy the executable jar file into the container
COPY target/crawler-*.jar app.jar

# Expose the port that your Spring Boot app runs on
EXPOSE 8080

# Run the jar file
ENTRYPOINT ["java", "-jar", "app.jar"]