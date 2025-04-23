# ZephyrCentrum

A Spring Boot web application with REST API endpoints, JPA for data persistence, and security features.

## Prerequisites

- Java 21
- PostgreSQL database
- Maven (or use the included Maven wrapper)

## Project Structure

- `src/main/java`: Java source code
  - `com.ctang.zephyrcentrum`: Main application package
    - `controllers`: REST API endpoints
    - `models`: Entity classes
    - `services`: Business logic
    - `repositories`: Data access layer
- `src/main/resources`: Configuration files
  - `application.yml`: Core application settings
  - `application-dev.yml`: Development environment settings
  - `application-prod.yml`: Production environment settings
  - `application-test.yml`: Testing environment settings

## Running the Application

### Using Maven Wrapper

1. Make sure PostgreSQL is running and accessible with the credentials in your config
2. Run the application:

   ```bash
   # On Windows
   ./mvnw.cmd spring-boot:run

   # On Linux/Mac
   ./mvnw spring-boot:run
   ```

3. The application will start on port 8080 (as configured in application-dev.yml)

### Using Maven directly

If you have Maven installed:

```bash
mvn spring-boot:run
```

## Configuration

The application uses different configuration profiles:
- `dev`: Development environment (default)
- `prod`: Production environment
- `test`: Testing environment

To run with a specific profile:

```bash
./mvnw spring-boot:run -Dspring.profiles.active=prod
```

## Dependency Management

The `pom.xml` file contains all the dependencies for the project. Maven will download these dependencies automatically when building the project.

- Spring Boot 3.4.4
- Spring Data JPA
- Spring Security
- PostgreSQL driver
- Lombok
- Bucket4j (for rate limiting)
- OAuth2 (authentication)