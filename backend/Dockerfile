# ── Stage 1: build ────────────────────────────────────────────────────────────
FROM eclipse-temurin:17-jdk-alpine AS build

WORKDIR /workspace

# Copy Maven wrapper and POM first so dependency layer is cached separately
COPY mvnw mvnw.cmd pom.xml ./
COPY .mvn .mvn

# Download dependencies (layer cached unless pom.xml changes)
RUN ./mvnw dependency:go-offline -q

# Copy source and build the fat JAR, skipping tests (tests run separately in CI)
COPY src src
RUN ./mvnw package -DskipTests -q

# ── Stage 2: runtime ──────────────────────────────────────────────────────────
FROM eclipse-temurin:17-jre-alpine AS runtime

# Non-root user for security
RUN addgroup -S spring && adduser -S spring -G spring
USER spring

WORKDIR /app

# Copy only the fat JAR from the build stage
COPY --from=build /workspace/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
