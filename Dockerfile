# 1-й етап: збірка з Maven
FROM maven:3.9.4-eclipse-temurin-17 AS build

WORKDIR /app

# Копіюємо pom.xml та завантажуємо залежності (кешування)
COPY pom.xml .
RUN mvn dependency:go-offline

# Копіюємо весь код і збираємо jar
COPY src ./src
RUN mvn clean package -DskipTests

# 2-й етап: запуск програми
FROM openjdk:17-jdk-slim

WORKDIR /app

# Копіюємо jar з попереднього етапу
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
