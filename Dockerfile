# 1) Build 단계
FROM eclipse-temurin:21-jdk AS builder
WORKDIR /app

# 캐시 효율을 위해 build.gradle, settings.gradle 먼저 복사
COPY build.gradle settings.gradle gradlew ./
COPY gradle ./gradle
RUN chmod +x gradlew

# 소스 복사 & bootJar 생성
COPY . .
RUN ./gradlew bootJar --no-daemon

# Runtime stage
FROM eclipse-temurin:21-jre AS runtime
WORKDIR /app

# 서비스 포트 문서화
EXPOSE 8080

# 빌드된 JAR만 복사
COPY --from=builder /app/build/libs/*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]