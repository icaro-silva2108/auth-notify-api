FROM maven:3.9-eclipse-temurin AS builder

WORKDIR /app

COPY . .

RUN ./mvnw clean package -e

FROM eclipse-temurin:21-jre

COPY --from=builder /app/target/auth-notify-api-0.0.1-SNAPSHOT.jar app.jar

CMD ["java", "-jar", "app.jar"]

EXPOSE 8080