# Use an official OpenJDK runtime as the base image
FROM openjdk:17-jdk-slim  # Changed to Java 17 for compatibility, but you can revert to 21 if required

# Set the working directory inside the container
WORKDIR /app

# Copy only necessary files for a faster build
COPY build.gradle settings.gradle gradlew* ./
COPY gradle ./gradle
COPY src ./src

# Give executable permissions to the Gradle wrapper
RUN chmod +x ./gradlew

# Build the application, generating the shadow JAR
RUN ./gradlew clean shadowJar --info --stacktrace --warning-mode all

# Expose the port your app runs on (ensure this matches your app's port)
EXPOSE 8080

# Command to run the shadow JAR file
CMD ["java", "-jar", "build/libs/HeroAcademyGym-1.0-SNAPSHOT.jar"]
