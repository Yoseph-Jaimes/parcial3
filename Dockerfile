# Etapa 1: Compilar la aplicación
FROM maven:3.8.5-openjdk-17 AS build
COPY . .
RUN mvn clean package -DskipTests

# Etapa 2: Ejecutar la aplicación
FROM eclipse-temurin:17-jdk-alpine
COPY --from=build /target/parcial3-1.jar app.jar
EXPOSE 8110
ENTRYPOINT ["java", "-jar", "app.jar"]