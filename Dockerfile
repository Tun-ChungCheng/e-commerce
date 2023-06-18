# Fetching 1.8 version of Java
FROM openjdk:8

# Setting up work directory
WORKDIR /app

# Copy the jar file into our app
COPY ./target/e-commerce-docker.jar /app

# Exposing port 8080
EXPOSE 8080

# Starting the application
CMD ["java", "-jar", "e-commerce-docker.jar"]