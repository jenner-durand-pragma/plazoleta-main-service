# Stage 1: Build with Gradle
FROM amazoncorretto:11-alpine-jdk AS builder

WORKDIR /workspace/app

COPY gradlew .
COPY gradle gradle
COPY build.gradle settings.gradle lombok.config ./

RUN chmod +x gradlew && ./gradlew dependencies --no-daemon || true

COPY src src

RUN ./gradlew clean bootJar --no-daemon -x test

# Stage 2: Runtime
FROM amazoncorretto:11-alpine

WORKDIR /app

RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

COPY --from=builder /workspace/app/build/libs/*.jar app.jar

EXPOSE 8092

ENTRYPOINT ["java", "-jar", "/app/app.jar"]