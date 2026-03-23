# Gato Backend

Gato is a Spring Boot 3 API for a gamified todo application. It combines JWT-based authentication, todo tracking, streaks, points, badge awards, and admin management tools in a single PostgreSQL-backed service.

## Features

- User registration and login
- JWT-secured API
- Password reset token flow
- Todo creation, listing, and completion
- Automatic points and streak updates
- Badge awards driven by rule thresholds
- Admin APIs for users and badges
- Flyway database migrations

## Stack

- Java 17
- Spring Boot 3.5
- Spring Web, Security, Validation, Data JPA, Mail
- PostgreSQL
- Flyway
- JJWT
- Docker Compose

## Running Locally

### Docker Compose

```bash
docker compose up --build
```

The API will be available at `http://localhost:8080`.

### Maven

Make sure PostgreSQL is running and a database like `gato_db` exists, then run:

```bash
./mvnw spring-boot:run
```

## Environment Variables

| Variable | Required | Description |
| --- | --- | --- |
| `SPRING_DATASOURCE_URL` | Yes | PostgreSQL JDBC connection string |
| `SPRING_DATASOURCE_USERNAME` | Yes | PostgreSQL username |
| `SPRING_DATASOURCE_PASSWORD` | Yes | PostgreSQL password |
| `APP_JWT_SECRET` | Yes | JWT signing secret, minimum 32 bytes |
| `APP_JWT_EXPIRYMINUTES` | No | JWT token lifetime, default `60` |
| `APP_PASSWORDRESET_TOKENTTLMINUTES` | No | Reset token lifetime, default `30` |
| `SMTP_HOST` | No | SMTP host for password reset emails |
| `SMTP_PORT` | No | SMTP port, default `587` |
| `SMTP_USERNAME` | No | SMTP username |
| `SMTP_PASSWORD` | No | SMTP password |
| `MAIL_FROM_ADDRESS` | No | Sender email address |
| `MAIL_FROM_NAME` | No | Sender display name |

The provided [docker-compose.yml](/home/chibueze-evans-okocha/codes/java/gato/docker-compose.yml) sets local defaults for the database and JWT secret.

## API Overview

Base path: `/api`

### Public Endpoints

- `POST /auth/register`
- `POST /auth/login`
- `POST /auth/forgot-password`
- `POST /auth/reset-password`

### Authenticated Endpoints

- `GET /me`
- `GET /todos`
- `POST /todos`
- `PATCH /todos/{id}/complete`

### Admin Endpoints

- `GET /admin/users`
- `GET /admin/users/{id}`
- `PUT /admin/users/{id}`
- `PATCH /admin/users/{id}/admin`
- `DELETE /admin/users/{id}`
- `GET /admin/badges`
- `GET /admin/badges/{id}`
- `POST /admin/badges`
- `PUT /admin/badges/{id}`
- `DELETE /admin/badges/{id}`

## Authentication

Protected endpoints expect a bearer token:

```http
Authorization: Bearer <jwt>
```

Public routes are configured in [SecurityConfig.java](/home/chibueze-evans-okocha/codes/java/gato/src/main/java/com/example/gato/config/SecurityConfig.java). Admin routes require `ADMIN` authority.

## Gamification Behavior

When a user completes a todo for the first time, the service:

- Marks the todo as complete
- Stores the completion timestamp
- Adds 10 points to the user
- Starts or increments the streak counter
- Re-evaluates badge awards

Seeded badges include:

- `FIRST_DONE`
- `STREAK_7`

Badge rules can also be managed through the admin API using minimum points, streak count, and completed todo thresholds.

## Response and Error Notes

- Validation errors return `400` with an `error` message.
- Invalid login returns `401`.
- Forbidden admin access returns `403`.
- Duplicate email or badge code returns `409`.
- Missing entities return `404`.

## Project References

- [HELP.md](/home/chibueze-evans-okocha/codes/java/gato/HELP.md) for the fuller operator guide
- [application.yml](/home/chibueze-evans-okocha/codes/java/gato/src/main/resources/application.yml) for runtime defaults
- [V2__seed_badges.sql](/home/chibueze-evans-okocha/codes/java/gato/src/main/resources/db/migration/V2__seed_badges.sql) for seeded badges
