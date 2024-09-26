# Use OpenJDK as the base image
FROM openjdk:21-jdk-slim

# Set the working directory inside the container
WORKDIR /app

# Copy Gradle build scripts and settings first to leverage Docker cache
COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .

# Run Gradle build to create the JAR
RUN ./gradlew build

# Now copy the JAR file into the container
COPY ./build/libs/WrathFitnessPet-1.0-SNAPSHOT.jar /app/WrathFitnessPet.jar

# Expose the port your app will run on
EXPOSE 8080

# Command to run the jar file
ENTRYPOINT ["java", "-jar", "WrathFitnessPet.jar"]
