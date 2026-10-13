FROM maven:3.9.6-eclipse-temurin-17-alpine AS builder
WORKDIR /app

# Copy files from build context (works whether context is repo root or /backend)
COPY . .

# Build jar dynamically regardless of whether context is root or /backend
RUN if [ -f "backend/pom.xml" ]; then \
      cd backend && mvn clean package -DskipTests && cp target/*.jar /app/app.jar; \
    else \
      mvn clean package -DskipTests && cp target/*.jar /app/app.jar; \
    fi

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=builder /app/app.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-XX:MaxRAMPercentage=75.0", "-jar", "/app/app.jar"]
