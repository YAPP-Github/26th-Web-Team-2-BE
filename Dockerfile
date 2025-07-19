# Build
FROM eclipse-temurin:21-jdk AS builder
WORKDIR /app

COPY build.gradle settings.gradle gradlew ./
COPY gradle ./gradle
RUN chmod +x gradlew

COPY . .
RUN ./gradlew bootJar --no-daemon

# Runtime stage
FROM eclipse-temurin:21-jre AS runtime
WORKDIR /app

EXPOSE 8080

COPY --from=builder /app/build/libs/*.jar app.jar
ENTRYPOINT ["sh","-c","java -Dserver.port=${PORT:-8080} -jar app.jar"]
