# Use a lightweight base image with Java (OpenJDK)
FROM eclipse-temurin:21-jre-jammy

# Set the working directory inside the container
WORKDIR /app

# Copy the executable JAR file into the container
COPY target/botsvb-1.0-SNAPSHOT-jar-with-dependencies.jar app.jar
COPY ./pwd.txt /app/
COPY ./gdrive.json /app/

COPY ./client_new_ca.jks /app/
COPY ./client_new_key.jks /app/

# (Optional) If your app uses external config files, uncomment:
# COPY config/ ./config/

# Expose the port your app runs on (e.g., Spring Boot default: 8080)
EXPOSE 443

# Run the JAR file when the container starts
ENTRYPOINT ["java", "-jar", "app.jar"]