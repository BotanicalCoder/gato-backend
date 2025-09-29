# Gato: Empower Your Productivity with Gamified Todos 🚀

## Overview

Gato is a modern Spring Boot backend application designed to gamify your daily tasks. It provides a robust RESTful API for user authentication, personalized todo management, and an engaging gamification system that rewards users with points, streaks, and badges for their accomplishments. Built with a focus on clean architecture and security, Gato leverages industry-standard technologies to deliver a high-performance and scalable solution for boosting user productivity. ✨

## Features

- **User Authentication**: Secure registration, login, and password management with JWT tokens.
- **Todo Management**: Create, retrieve, and mark tasks as complete.
- **Gamification System**: Earn points, build streaks, and unlock unique badges for completing todos.
- **JWT-based Security**: Protect API endpoints with stateless JSON Web Tokens.
- **PostgreSQL Database**: Persistent storage for all application data.
- **Flyway Database Migrations**: Manage database schema evolution reliably.
- **Comprehensive Error Handling**: Graceful error responses for validation, authentication, and other issues.
- **Dockerized Deployment**: Easy setup and deployment using Docker and Docker Compose.

## Getting Started

To get the Gato API running locally, follow these steps:

### Installation

1.  **Clone the Repository**:
    ```bash
    git clone <repository-url>
    cd gato
    ```
2.  **Start with Docker Compose**:
    The project includes a `docker-compose.yml` file for easy setup of both the PostgreSQL database and the Spring Boot application.
    ```bash
    docker-compose up --build
    ```
    This command will:
    - Build the Docker image for the Spring Boot application.
    - Start a PostgreSQL container.
    - Apply Flyway database migrations automatically.
    - Start the Gato API server, accessible on `http://localhost:8080`.

### Environment Variables

The application requires the following environment variables to be configured. When running with `docker-compose up`, these are automatically picked up from the `docker-compose.yml` file. For local development outside Docker, you would set these in your `application.yml` or `application.properties` or as system environment variables.

- `SPRING_DATASOURCE_URL`: JDBC URL for the PostgreSQL database.
  - **Example**: `jdbc:postgresql://db:5432/gato_db` (for Docker Compose)
  - **Example**: `jdbc:postgresql://localhost:5432/gato_db` (for local run)
- `SPRING_DATASOURCE_USERNAME`: Database username.
  - **Example**: `gato`
- `SPRING_DATASOURCE_PASSWORD`: Database password.
  - **Example**: `gato`
- `APP_JWT_SECRET`: A long, random string (at least 32 bytes) used for signing JWT tokens. **CRITICAL for security! Change this in production.**
  - **Example**: `0123456789abcdef0123456789abcdef0123456789abcdef0123456789ab`
- `APP_JWT_EXPIRYMINUTES`: Expiration time for JWT tokens in minutes.
  - **Example**: `60`
- `APP_PASSWORDRESET_TOKENTTLMINUTES`: Time-to-live for password reset tokens in minutes.
  - **Example**: `30`

## API Documentation

### Base URL

`http://localhost:8080/api`

### Endpoints

#### `POST /api/auth/register`

Registers a new user account.

**Request**:

```json
{
  "email": "user@example.com",
  "displayName": "John Doe",
  "password": "StrongPassword123!"
}
```

**Response**:

```json
{
  "message": "Registered",
  "success": true,
  "data": {}
}
```

**Errors**:

- `400 Bad Request`: Validation errors (e.g., invalid email, password not meeting complexity requirements).
  ```json
  {
    "error": "password Password must contain at least 1 uppercase, 1 lowercase, 1 number, and 1 special character."
  }
  ```
- `409 Conflict`: Email already in use.
  ```json
  {
    "message": "Email already in use",
    "success": false,
    "data": {}
  }
  ```

#### `POST /api/auth/login`

Authenticates a user and returns a JWT token.

**Request**:

```json
{
  "email": "user@example.com",
  "password": "StrongPassword123!"
}
```

**Response**:

```json
{
  "message": "Logged in",
  "success": true,
  "data": {
    "token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ1c2VyQGV4YW1wbGUuY29tIiwiaWF0IjoxNzE5NTU0MjA3LCJleHAiOjE3MTk1NTc4MDd9.EXAMPLE_JWT_TOKEN"
  }
}
```

**Errors**:

- `401 Unauthorized`: Invalid credentials.
  ```json
  {
    "success": false,
    "message": "Invalid credentials"
  }
  ```

#### `POST /api/auth/forgot-password`

Initiates a password reset process by sending a token to the user's email.

**Request**:

```json
{
  "email": "user@example.com"
}
```

**Response**:

```json
{
  "message": "If the email exists, a reset token was sent."
}
```

**Errors**:

- `400 Bad Request`: Invalid email format.

#### `POST /api/auth/reset-password`

Resets the user's password using a provided token.

**Request**:

```json
{
  "token": "a-valid-reset-token-uuid",
  "newPassword": "NewStrongPassword456#"
}
```

**Response**:

```json
{
  "message": "Password reset"
}
```

**Errors**:

- `400 Bad Request`: Invalid, expired, or used token.
  ```json
  {
    "error": "Invalid token"
  }
  ```
  ```json
  {
    "error": "Token invalid or expired"
  }
  ```

#### `GET /api/me`

Retrieves the profile information for the authenticated user.

**Request**:
Requires `Authorization: Bearer <JWT_TOKEN>` header.

**Response**:

```json
{
  "email": "user@example.com",
  "displayName": "John Doe",
  "points": 50,
  "streakCount": 7,
  "badges": ["FIRST_DONE", "STREAK_7"]
}
```

**Errors**:

- `401 Unauthorized`: Missing or invalid JWT token.

#### `GET /api/todos`

Retrieves a list of all todos for the authenticated user.

**Request**:
Requires `Authorization: Bearer <JWT_TOKEN>` header.

**Response**:

```json
[
  {
    "id": "c1f7b0a8-3d5e-4e6f-8a9b-2c1e0d3f4a5b",
    "title": "Buy groceries",
    "notes": "Milk, eggs, bread",
    "dueOn": "2024-07-01",
    "done": false,
    "doneAt": null
  },
  {
    "id": "d2g8c1b9-4e6f-5g7h-9b0c-3d2f1e4g5a6c",
    "title": "Finish report",
    "notes": "Section 3 and conclusion",
    "dueOn": "2024-06-28",
    "done": true,
    "doneAt": "2024-06-27T10:30:00Z"
  }
]
```

**Errors**:

- `401 Unauthorized`: Missing or invalid JWT token.

#### `POST /api/todos`

Creates a new todo for the authenticated user.

**Request**:
Requires `Authorization: Bearer <JWT_TOKEN>` header.

```json
{
  "title": "New important task",
  "notes": "Remember to prepare for the meeting.",
  "dueOn": "2024-07-15"
}
```

**Response**:

```json
{
  "id": "e3h9d2c0-5f7g-6h8i-0c1d-4e3g2f5h6a7d",
  "title": "New important task",
  "notes": "Remember to prepare for the meeting.",
  "dueOn": "2024-07-15",
  "done": false,
  "doneAt": null
}
```

**Errors**:

- `400 Bad Request`: Validation errors (e.g., missing title).
- `401 Unauthorized`: Missing or invalid JWT token.

#### `PATCH /api/todos/{id}/complete`

Marks a specific todo as complete for the authenticated user, awarding points, updating streak, and potentially awarding badges.

**Request**:
Requires `Authorization: Bearer <JWT_TOKEN>` header.
The `{id}` in the path should be the UUID of the todo.
No request body is required.

**Response**:

```json
{
  "id": "c1f7b0a8-3d5e-4e6f-8a9b-2c1e0d3f4a5b",
  "title": "Buy groceries",
  "notes": "Milk, eggs, bread",
  "dueOn": "2024-07-01",
  "done": true,
  "doneAt": "2024-06-27T11:45:00Z"
}
```

**Errors**:

- `401 Unauthorized`: Missing or invalid JWT token.
- `404 Not Found`: Todo with the given ID not found for the authenticated user.

## Usage

Once the Gato API is running (e.g., via `docker-compose up`), you can interact with it using any HTTP client (like Postman, Insomnia, `curl`, or a frontend application).

1.  **Register a New User**: Send a `POST` request to `/api/auth/register` with your email, display name, and a strong password.
2.  **Log In**: Send a `POST` request to `/api/auth/login` with your registered email and password to receive a JWT token.
3.  **Authenticate Requests**: Include the obtained JWT token in the `Authorization` header of subsequent requests: `Authorization: Bearer <YOUR_JWT_TOKEN>`.
4.  **Manage Todos**:
    - `GET /api/todos` to view your current tasks.
    - `POST /api/todos` to add a new task.
    - `PATCH /api/todos/{id}/complete` to mark a task as done and earn rewards!
5.  **View Profile**: `GET /api/me` to see your points, streak, and awarded badges.

## Technologies Used

| Technology          | Description                                          | Link                                                          |
| :------------------ | :--------------------------------------------------- | :------------------------------------------------------------ |
| **Java 17+**        | Primary programming language                         | [Oracle Java](https://www.oracle.com/java/)                   |
| **Spring Boot 3**   | Framework for building enterprise-grade applications | [Spring Boot](https://spring.io/projects/spring-boot)         |
| **Spring Security** | Authentication and authorization framework           | [Spring Security](https://spring.io/projects/spring-security) |
| **Spring Data JPA** | Data access layer with Hibernate                     | [Spring Data JPA](https://spring.io/projects/spring-data-jpa) |
| **PostgreSQL**      | Robust relational database                           | [PostgreSQL](https://www.postgresql.org/)                     |
| **Flyway**          | Database migration tool                              | [Flyway](https://flywaydb.org/)                               |
| **JJWT**            | JSON Web Token library                               | [JJWT GitHub](https://github.com/jwtk/jjwt)                   |
| **Lombok**          | Boilerplate code reduction                           | [Project Lombok](https://projectlombok.org/)                  |
| **Maven**           | Build automation tool                                | [Apache Maven](https://maven.apache.org/)                     |
| **Docker**          | Containerization platform                            | [Docker](https://www.docker.com/)                             |

## Contributing

We welcome contributions to Gato! If you're interested in improving this project, please consider the following guidelines:

- Fork the repository.
- Create a new branch for your feature or bug fix.
- Ensure your code adheres to the project's coding style and passes all tests.
- Submit a pull request with a clear description of your changes.

## Author Info

- **Chibueze Evans Okocha**
  - LinkedIn: [Chibueze Evans Okocha](https://www.linkedin.com/in/chibueze-okocha-749a291a8?utm_source=share&utm_campaign=share_via&utm_content=profile&utm_medium=ios_app)
  - Twitter: [@chisageo](https://twitter.com/chisageo)

---

[![Build Status](https://img.shields.io/badge/build-passing-brightgreen)](https://github.com/yourusername/gato/actions)
[![License](https://img.shields.io/badge/License-MIT-blue.svg)](https://opensource.org/licenses/MIT)
[![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.2.0-6DB33F?logo=spring)](https://spring.io/projects/spring-boot)
[![Java](https://img.shields.io/badge/Java-17-orange?logo=java)](https://www.java.com/)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-4254FF?logo=postgresql&logoColor=white)](https://www.postgresql.org/)
[![Docker](https://img.shields.io/badge/Docker-2496ED?logo=docker&logoColor=white)](https://www.docker.com/)

[![Readme was generated by Dokugen](https://img.shields.io/badge/Readme%20was%20generated%20by-Dokugen-brightgreen)](https://www.npmjs.com/package/dokugen)
