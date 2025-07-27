# -------------------------------
# Stage 1: Сборка с Maven
# -------------------------------
FROM --platform=linux/amd64 maven:3.9.6-eclipse-temurin-17-alpine AS builder

WORKDIR /app

COPY pom.xml .
RUN mvn dependency:go-offline -B

COPY src ./src
RUN mvn package -DskipTests -B

# -------------------------------
# Stage 2: Финальный образ
# -------------------------------
FROM --platform=linux/amd64 eclipse-temurin:17-jre-alpine

# Создаём директорию ДО chown
RUN mkdir -p /app && \
    adduser -D -s /bin/sh appuser && \
    chown -R appuser:appuser /app && \
    apk add redis

WORKDIR /app

# Копируем JAR из builder
COPY --from=builder --chown=appuser:appuser /app/target/*.jar app.jar

# Переходим на непривилегированного пользователя
USER appuser

# Запускаем приложение
ENTRYPOINT ["java", "-jar", "app.jar"]