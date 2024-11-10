# First stage: Build the application
FROM openjdk:17-jdk-slim as builder

# Set the working directory inside the container
WORKDIR /app

# Copy only Gradle wrapper and project metadata first (to leverage caching)
COPY build.gradle settings.gradle gradlew* ./
COPY gradle ./gradle

# Give executable permissions to the Gradle wrapper
RUN chmod +x ./gradlew

# Download dependencies (this layer will be cached if no changes are made)
RUN ./gradlew dependencies --no-daemon

# Copy the source files last
COPY src ./src

# Build the application, generating the shadow JAR
RUN ./gradlew clean shadowJar --info --stacktrace --warning-mode all --no-daemon

# Second stage: Create a smaller runtime image
FROM openjdk:17-jre-slim

# Set the working directory inside the container
WORKDIR /app

# Copy the generated JAR file from the builder stage
COPY --from=builder /app/build/libs/*.jar app.jar

# Expose the port your app runs on (ensure this matches your app's port)
EXPOSE 8080

# Command to run the shadow JAR file
CMD ["java", "-jar", "app.jar"]
