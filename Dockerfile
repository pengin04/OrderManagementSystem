FROM maven:3-eclipse-temurin-21 AS build
COPY . .
RUN mvn clean package -Dmaven.test.skip=true
FROM eclipse-temurin:21-alpine
COPY --from=build /target/OrderManagementSystem-0.0.1-SNAPSHOT.jar OrderManagementSystem.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "OrderManagementSystem.jar"]
