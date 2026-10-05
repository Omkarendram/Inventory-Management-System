# Multi-stage Dockerfile for Enterprise Spring Boot Application
# Stage 1: Build stage
FROM eclipse-temurin:17-jdk-jammy AS builder
WORKDIR /workspace

# Copy maven wrapper and pom.xml first to optimize layer caching
COPY .mvn .mvn
COPY mvnw pom.xml ./
RUN ./mvnw dependency:go-offline -B || true

# Copy source code and build
COPY src src
RUN ./mvnw clean package -DskipTests=false -B

# Extract jar layers for optimal container caching (Spring Boot layered jar)
RUN java -Djarmode=tools -jar target/*.jar extract --layers --launcher --destination layers/ 2>/dev/null || \
    mkdir -p layers && cp target/*.jar layers/app.jar

# Stage 2: Production runtime stage
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

# Create non-root system user and group for security
RUN groupadd -r spring && useradd -r -g spring spring

# Copy artifacts from build stage
COPY --from=builder --chown=spring:spring /workspace/target/*.jar /app/app.jar

USER spring:spring

EXPOSE 9090

# Standard JVM container flags
ENV JAVA_OPTS="-XX:MaxRAMPercentage=75.0 -XX:+UseG1GC -Djava.security.egd=file:/dev/./urandom"
ENV SERVER_PORT=9090

HEALTHCHECK --interval=30s --timeout=5s --start-period=60s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:${PORT:-9090}/login || exit 1

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar"]
