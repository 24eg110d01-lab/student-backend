# Stage 1: Build JAR with Maven and OpenJDK 21
FROM maven:3.9.6-eclipse-temurin-21-alpine AS builder
WORKDIR /app

# Cache dependencies
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Build application
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Runtime Image
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Create non-root user for security
RUN addgroup -S appgroup && adduser -S appuser -G appgroup
USER appuser

COPY --from=builder /app/target/student-management-backend-1.0.0.jar app.jar

ENV PORT=8080
ENV SPRING_DATA_MONGODB_URI=mongodb://mongodb:27017/student_management

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
