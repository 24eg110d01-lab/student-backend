# Student Management System - Backend API

Spring Boot 3.3.4 RESTful API for the Student Management System.

## Tech Stack
- **Java 21 LTS**
- **Spring Boot 3.3.4**
- **Spring Security 6 with JWT**
- **Spring Data MongoDB**
- **Lombok**
- **Bean Validation**
- **Springdoc OpenAPI (Swagger UI)**

## Getting Started

### Prerequisites
- Java 21 LTS (`java -version`)
- Maven 3.9+ (`mvn -v`)
- MongoDB running on `mongodb://localhost:27017`

### Running Locally
```bash
mvn clean spring-boot:run
```
The server will start at `http://localhost:8080`.

### Documentation
- **Swagger UI**: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
- **OpenAPI JSON**: [http://localhost:8080/v3/api-docs](http://localhost:8080/v3/api-docs)

### Running Tests
```bash
mvn test
```

### Default Credentials
- **Admin**: `admin@school.com` / `Admin@123`
- **Teacher**: `teacher@school.com` / `Teacher@123`
- **Student**: `student@school.com` / `Student@123`