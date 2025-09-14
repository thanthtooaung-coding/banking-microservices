# build stage
FROM maven:3.9.4-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn -DskipTests package

# runtime stage
FROM eclipse-temurin:17-jre-focal
WORKDIR /app
COPY --from=build /app/target/user-service-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8081 9090
ENTRYPOINT ["java","-jar","/app/app.jar"]
