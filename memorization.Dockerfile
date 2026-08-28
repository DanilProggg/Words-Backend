# Multi-stage build for the "memorization" (domain/learning) microservice.
# The source repo ships without a Dockerfile, so this is a minimal build/run setup
# for local docker-compose use only.

FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /build
COPY memorization/pom.xml .
RUN mvn -q -B dependency:go-offline
COPY memorization/src ./src
RUN mvn -q -B clean package -DskipTests

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /build/target/memorization-*.jar app.jar
EXPOSE 9091
ENTRYPOINT ["java", "-jar", "app.jar"]
