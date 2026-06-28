FROM maven:3.9.11-eclipse-temurin-21-alpine AS builder

WORKDIR /workspace

COPY pom.xml ./
RUN mvn dependency:go-offline -B

COPY src ./src
RUN mvn clean package -B -DskipTests

FROM eclipse-temurin:21-jre-alpine AS runtime

RUN addgroup -S spring && adduser -S spring -G spring

WORKDIR /app

COPY --from=builder --chown=spring:spring /workspace/target/*.jar app.jar

USER spring:spring

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
