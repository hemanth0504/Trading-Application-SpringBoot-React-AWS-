# ---- Build Stage ----
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline -B
# Copy source code
COPY src ./src

RUN mvn -q -DskipTests clean package

# ---- Run Stage ----
FROM eclipse-temurin:21-jre
WORKDIR /opt/app

# Copy the built JAR from the build stage
COPY --from=build /app/target/*SNAPSHOT.jar app.jar

# Expose port (same as in your application.properties)
EXPOSE 8080

# JVM options for containerized environment
ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75"

# Run Spring Boot
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /opt/app/app.jar"]
