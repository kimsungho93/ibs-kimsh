# Build stage
FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /app

# Copy gradle wrapper and build files
COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .

# Copy source code
COPY src src

# Build application
RUN chmod +x ./gradlew
RUN ./gradlew clean bootJar -x test --no-daemon

# Runtime stage
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Copy jar from build stage
COPY --from=build /app/build/libs/app.jar app.jar

# Expose port (Railway will override with $PORT)
EXPOSE 8080

# Run application with Railway PORT support
CMD ["sh", "-c", "java -Dserver.port=${PORT:-8080} -Duser.timezone=Asia/Seoul -jar app.jar"]
