# Multi-stage Dockerfile optimized for Render & Cloud deployments (512MB RAM friendly)
# Stage 1: Build stage
FROM eclipse-temurin:17-jdk-alpine AS build
WORKDIR /app

# Copy Maven wrapper and configuration
COPY pom.xml .
COPY .mvn .mvn
COPY mvnw .
RUN chmod +x ./mvnw

# Copy source and build executable jar
COPY src src
RUN ./mvnw clean package -DskipTests -B

# Stage 2: Lightweight production runtime stage
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Create non-root system user for container security
RUN addgroup -S spring && adduser -S spring -G spring

# Copy jar artifact from build stage
COPY --from=build --chown=spring:spring /app/target/*.jar app.jar

USER spring:spring

EXPOSE 8080

# Tune JVM heap memory for Render's 512MB free tier container limit
ENV JAVA_OPTS="-Xmx384m -Xms128m -XX:+UseG1GC -Djava.security.egd=file:/dev/./urandom"

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -Dserver.port=${PORT:-8080} -jar app.jar"]
