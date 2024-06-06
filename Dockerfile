# Use Ubuntu as the base image for the build stage
FROM ubuntu:latest AS build

# Update the package list and install OpenJDK
RUN apt-get update && \
    apt-get install -y openjdk-17-jdk

# Set the working directory in the container
WORKDIR /app

# Copy the project files into the container
COPY . .

# Give execution rights on the Gradle wrapper
RUN chmod +x ./gradlew

# Build the project using the Gradle wrapper
RUN ./gradlew bootJar --no-daemon

# Start the second stage of the build process using a slim version of OpenJDK
FROM openjdk:17-jdk-slim

# Expose port 8080 for the application
EXPOSE 8080

# Set the working directory in the container
WORKDIR /app

# Copy the built jar file from the build stage into the current container
COPY --from=build /app/build/libs/*.jar app.jar

# Set the container's entrypoint, this is the command that will run when the container starts
ENTRYPOINT ["java", "-jar", "app.jar"]
