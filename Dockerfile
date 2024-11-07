# Use an official OpenJDK runtime as the base image
FROM openjdk:21-jdk-slim

# Set the working directory inside the container
WORKDIR /app

# Copy the project files into the container
COPY . .

# Give executable permissions to the Gradle wrapper
RUN chmod +x ./gradlew

# Build the application
RUN ./gradlew clean build --info --stacktrace --warning-mode all 

# Expose the port your app runs on (ensure this matches your app's port)
EXPOSE 8080

# Command to run the application
CMD ["java", "-jar", "build/libs/WrathFitnessPet-1.0-SNAPSHOT.jar"]

