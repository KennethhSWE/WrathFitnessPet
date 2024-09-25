# Use OpenJDK 21 as the base image
FROM openjdk:21-jdk-slim

# Set the working directory inside the container
WORKDIR /app

# Copy the build files into the container
COPY ./build/libs/*.jar /app/WrathFitnessPet.jar

# Expose the port your app will run on
EXPOSE 8080

# Command to run the jar file
ENTRYPOINT ["java", "-jar", "WrathFitnessPet.jar"]
