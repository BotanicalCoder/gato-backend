# Gato: Gamified Todo Backend API 🎯

This project is a robust Spring Boot backend for a gamified todo application. It provides a complete API for user authentication, managing todos, and incorporates a unique gamification system rewarding users with points and badges for their productivity and consistency. Built with modern Java standards, Spring Security, and a PostgreSQL database, it's designed for scalability and maintainability.

---

## 🚀 Getting Started

Follow these steps to set up the Gato API backend on your local machine.

### Installation

For the easiest setup, use Docker Compose:

- 🐳 **Ensure Docker is Running**: Make sure Docker Desktop or Docker Engine is installed and running on your system.
- 📦 **Clone the Repository**:
  ```bash
  git clone https://github.com/BotanicalCoder/gato-backend.git
  cd gato-backend
  ```
- 🛠️ **Build and Run with Docker Compose**:
  ```bash
  docker compose up --build -d
  ```
  This command will build the Docker image, set up a PostgreSQL database, and start the Spring Boot application, making it accessible on `http://localhost:8080`.

**Alternatively, to run directly using Maven:**

- ☕ **Java Development Kit (JDK)**: Ensure you have JDK 17 or newer installed.
- 📦 **Clone the Repository**:
  ```bash
  git clone https://github.com/BotanicalCoder/gato-backend.git
  cd gato-backend
  ```
- ⚙️ **Database Setup**: Manually set up a PostgreSQL database. Ensure the database name, username, and password match your `application.yml` or `application.properties` configuration (or provide them as environment variables). Flyway will handle migrations on startup.
- 🚀 **Run the Application**:
  ```bash
  ./mvnw spring-boot:run
  ```

### Environment Variables

The application relies on several environment variables for configuration. When running with Docker Compose, these are defined in `docker-compose.yml`. When running locally with Maven, you can set them in your shell or define them in `src/main/resources/application.yml` or `application.properties`.

| Variable Name                       | Description                                                                                        | Example Value (Docker Compose)                                                            | Example Value (local `application.yml`)                                                                                                                 |
| :---------------------------------- | :------------------------------------------------------------------------------------------------- | :---------------------------------------------------------------------------------------- | :------------------------------------------------------------------------------------------------------------------------------------------------------ |
| `SPRING_DATASOURCE_URL`             | JDBC URL for the PostgreSQL database.                                                              | `jdbc:postgresql://db:5432/gato_db`                                                       | `jdbc:postgresql://localhost:5432/gato_db`                                                                                                              |
| `SPRING_DATASOURCE_USERNAME`        | Database username.                                                                                 | `gato`                                                                                    | `gato`                                                                                                                                                  |
| `SPRING_DATASOURCE_PASSWORD`        | Database password.                                                                                 | `gato`                                                                                    | `gato`                                                                                                                                                  |
| `APP_JWT_SECRET`                    | Secret key for signing JWT tokens. **Crucial for security, must be strong and at least 32 bytes.** | `0123456789abcdef0123456789abcdef0123456789abcdef0123456789ab` (long, randomly generated) | `'0123456789abcdef0123456789abcdef0123456789abcdef0123456789ab0123456789abcdef0123456789abcdef0123456789abcdef0123456789ab'` (long, randomly generated) |
| `APP_JWT_EXPIRYMINUTES`             | JWT token expiry duration in minutes.                                                              | `60`                                                                                      | `60`                                                                                                                                                    |
| `APP_PASSWORDRESET_TOKENTTLMINUTES` | Password reset token time-to-live in minutes.                                                      | `30`                                                                                      | `30`                                                                                                                                                    |

---

## 📖 API Documentation

### Base URL

The API root path for all endpoints is `http://localhost:8080/api`.

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

- `email` (string, required): User's email address. Must be a valid email format.
- `displayName` (string, required): User's display name. Cannot be blank.
- `password` (string, required): User's password. Must be at least 8 characters long and contain at least 1 uppercase, 1 lowercase, 1 number, and 1 special character.

**Response**:

```json
{
  "message": "Registered",
  "success": true,
  "data": {}
}
```

**Errors**:

- `400 Bad Request`: Validation error (e.g., invalid email format, weak password, blank display name).
- `409 Conflict`: Email address is already in use.

#### `POST /api/auth/login`

Authenticates a user and returns a JWT token.

**Request**:

```json
{
  "email": "user@example.com",
  "password": "StrongPassword123!"
}
```

- `email` (string, required): User's email address.
- `password` (string, required): User's password.

**Response**:

```json
{
  "message": "Logged in",
  "success": true,
  "data": {
    "token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ1c2VyQGV4YW1wbGUuY29tIiwiaWF0IjoxNjcyNTYwMDAwLCJleHAiOjE2NzI1NjM2MDB9.EXAMPLE_JWT_TOKEN"
  }
}
```

**Errors**:

- `400 Bad Request`: Validation error (e.g., invalid email format, blank password).
- `401 Unauthorized`: Invalid credentials (email or password incorrect).

#### `POST /api/auth/forgot-password`

Initiates the password reset process by sending a token to the user's email.

**Request**:

```json
{
  "email": "user@example.com"
}
```

- `email` (string, required): User's email address.

**Response**:

```json
{
  "message": "If the email exists, a reset token was sent."
}
```

(Always returns 200 OK to prevent email enumeration attacks).

**Errors**:

- `400 Bad Request`: Validation error (e.g., invalid email format).

#### `POST /api/auth/reset-password`

Resets the user's password using a provided token.

**Request**:

```json
{
  "token": "a1b2c3d4-e5f6-7890-1234-567890abcdef",
  "newPassword": "NewStrongPassword456!"
}
```

- `token` (string, required): The password reset token received via email.
- `newPassword` (string, required): The new password. Must meet the same complexity requirements as registration.

**Response**:

```json
{
  "message": "Password reset"
}
```

**Errors**:

- `400 Bad Request`: Validation error (e.g., blank token, weak new password).
- `400 Bad Request`: Invalid token (token does not exist, is used, or expired).

#### `GET /api/me`

Retrieves the authenticated user's profile information, including points, streak, and awarded badges.
Requires a valid JWT in the `Authorization` header.

**Request**: (No request body)

**Response**:

```json
{
  "email": "user@example.com",
  "displayName": "John Doe",
  "points": 150,
  "streakCount": 5,
  "badges": ["FIRST_DONE", "STREAK_7"]
}
```

**Errors**:

- `401 Unauthorized`: Missing or invalid JWT token.

#### `GET /api/todos`

Lists all todo items for the authenticated user.
Requires a valid JWT in the `Authorization` header.

**Request**: (No request body)

**Response**:

```json
[
  {
    "id": "1a2b3c4d-5e6f-7890-1234-567890abcdef",
    "title": "Buy groceries",
    "notes": "Milk, eggs, bread",
    "dueOn": "2024-07-20",
    "done": false,
    "doneAt": null
  },
  {
    "id": "f0e9d8c7-b6a5-4321-9876-543210fedcba",
    "title": "Finish report",
    "notes": "Final review and submission",
    "dueOn": "2024-07-18",
    "done": true,
    "doneAt": "2024-07-17T10:30:00Z"
  }
]
```

**Errors**:

- `401 Unauthorized`: Missing or invalid JWT token.

#### `POST /api/todos`

Creates a new todo item for the authenticated user.
Requires a valid JWT in the `Authorization` header.

**Request**:

```json
{
  "title": "New important task",
  "notes": "Remember to prepare for the meeting",
  "dueOn": "2024-07-25"
}
```

- `title` (string, required): Title of the todo item. Cannot be blank.
- `notes` (string, optional): Additional notes for the todo item.
- `dueOn` (string, optional, YYYY-MM-DD): The date when the todo is due.

**Response**:

```json
{
  "id": "1a2b3c4d-5e6f-7890-1234-567890abcdef",
  "title": "New important task",
  "notes": "Remember to prepare for the meeting",
  "dueOn": "2024-07-25",
  "done": false,
  "doneAt": null
}
```

**Errors**:

- `400 Bad Request`: Validation error (e.g., blank title).
- `401 Unauthorized`: Missing or invalid JWT token.

#### `PATCH /api/todos/{id}/complete`

Marks a specific todo item as complete for the authenticated user.
Awards points, updates streak, and potentially awards badges.
Requires a valid JWT in the `Authorization` header.

**Request**: (No request body)

**Response**:

```json
{
  "id": "1a2b3c4d-5e6f-7890-1234-567890abcdef",
  "title": "New important task",
  "notes": "Remember to prepare for the meeting",
  "dueOn": "2024-07-25",
  "done": true,
  "doneAt": "2024-07-17T11:45:00Z"
}
```

**Errors**:

- `401 Unauthorized`: Missing or invalid JWT token.
- `404 Not Found`: Todo item with the specified `id` does not exist or does not belong to the authenticated user.

---

## ✨ Features

- **User Authentication**: Secure user registration and login with JWT-based authentication.
- **Todo Management**: Create, view, and mark todo items as complete.
- **Gamification System**:
  - **Points**: Earn points for completing tasks.
  - **Streaks**: Track and reward daily task completion streaks.
  - **Badges**: Award badges for significant achievements (e.g., first task completed, 7-day streak).
- **Password Reset**: Secure mechanism for users to reset forgotten passwords via email tokens.
- **Robust Exception Handling**: Global exception handling for consistent error responses.
- **Docker Support**: Easy deployment and development using Docker and Docker Compose.
- **Database Migrations**: Managed database schema evolution using Flyway.
- **Security**: Implemented with Spring Security, bcrypt password hashing, and JWTs.

---

## 🛠️ Technologies Used

| Technology          | Version | Description                                                                     | Link                                                    |
| :------------------ | :------ | :------------------------------------------------------------------------------ | :------------------------------------------------------ |
| **Spring Boot**     | 3.5.5   | Framework for building stand-alone, production-grade Spring-based applications. | [Official Site](https://spring.io/projects/spring-boot) |
| **Spring Data JPA** | 3.5.5   | Simplifies database access and persistence with JPA and Hibernate.              | [Official Docs](https://docs.spring.io/spring-data/jpa) |
| **Spring Security** | 3.5.5   | Comprehensive security services for Java EE-based applications.                 | [Official Docs](https://docs.spring.io/spring-security) |
| **PostgreSQL**      | Latest  | Powerful, open-source object-relational database system.                        | [Official Site](https://www.postgresql.org/)            |
| **Flyway**          | Latest  | Database migration tool.                                                        | [Official Site](https://flywaydb.org/)                  |
| **JJWT**            | 0.11.5  | Java JWT: industry-standard JSON Web Token library.                             | [GitHub Repo](https://github.com/jwtk/jjwt)             |
| **Lombok**          | 1.18.32 | Reduces boilerplate code for Java classes.                                      | [Official Site](https://projectlombok.org/)             |
| **Docker**          | Latest  | Platform for developing, shipping, and running applications in containers.      | [Official Site](https://www.docker.com/)                |
| **Maven**           | 3.9.8   | Build automation tool for Java projects.                                        | [Official Site](https://maven.apache.org/)              |
| **Testcontainers**  | Latest  | Lightweight, disposable containers for tests.                                   | [Official Site](https://testcontainers.org/)            |

---

## 🤝 Contributing

We welcome contributions to improve Gato! If you have suggestions or want to contribute:

- ✨ **Fork the Repository**: Start by forking the project to your own GitHub account.
- 🌿 **Create a New Branch**: Create a feature branch for your changes (e.g., `feature/add-dark-mode`).
- 💡 **Implement Your Changes**: Write clean, well-tested code.
- 🧪 **Write Tests**: Ensure your new features or bug fixes are covered by tests.
- ⬆️ **Commit Your Changes**: Use clear and concise commit messages.
- 📝 **Open a Pull Request**: Submit a pull request detailing your changes and the problem it solves.

---

## 📄 License

This project is licensed under the MIT License. See the [LICENSE](LICENSE) file for details.

---

## ✍️ Author Info

**Chibueze Evans Okocha**

- LinkedIn: [Chibueze Evans Okocha](https://linkedin.com/in/chibueze-okocha-749a291a8)
- Twitter: [chisageo](https://twitter.com/chisageo)

---

## 🏅 Badges

[![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.5.5-6DB33F?style=for-the-badge&logo=spring&logoColor=white)](https://spring.io/projects/spring-boot)
[![Java](https://img.shields.io/badge/Java-17+-007396?style=for-the-badge&logo=java&logoColor=white)](https://www.java.com/)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-4169E1?style=for-the-badge&logo=postgresql&logoColor=white)](https://www.postgresql.org/)
[![Docker](https://img.shields.io/badge/Docker-2496ED?style=for-the-badge&logo=docker&logoColor=white)](https://www.docker.com/)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg?style=for-the-badge)](https://opensource.org/licenses/MIT)

[![Readme was generated by Dokugen](https://img.shields.io/badge/Readme%20was%20generated%20by-Dokugen-brightgreen)](https://www.npmjs.com/package/dokugen)
