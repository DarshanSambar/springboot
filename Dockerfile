# ---- Build Stage ----
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app

# Copy pom.xml and download dependencies first (layer cache)
COPY pom.xml .
RUN mvn dependency:go-offline -q

# Copy source and build the JAR (skip tests in Docker build)
COPY src ./src
RUN mvn package -DskipTests -q

# ---- Run Stage ----
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

# Copy only the built JAR from build stage
COPY --from=build /app/target/*.jar app.jar

# Expose the app port
EXPOSE 8888

# Run the app
ENTRYPOINT ["java", "-jar", "app.jar"]
