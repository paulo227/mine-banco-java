# ===== Stage 1: Build =====
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
# Baixar dependências primeiro (cache layer)
RUN mvn dependency:go-offline -q
COPY src ./src
RUN mvn clean package -DskipTests -q

# ===== Stage 2: Runtime =====
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

# Usuário não-root por segurança
RUN addgroup -S minebanco && adduser -S minebanco -G minebanco
USER minebanco

EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]
