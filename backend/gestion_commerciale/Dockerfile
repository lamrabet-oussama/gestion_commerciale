# Stage 1: Build the application
FROM maven:3.8.6-eclipse-temurin-17 AS build
WORKDIR /app

# Copy the pom.xml and install dependencies
COPY pom.xml .
RUN mvn dependency:go-offline

# Copy the source code and build the application
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Run the application
FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app

# Copy the JAR file from the build stage
COPY --from=build /app/target/*.jar app.jar

# Expose the port your Spring Boot app will run on
EXPOSE 8080

# Set the startup command
CMD ["java", "-jar", "app.jar"]
