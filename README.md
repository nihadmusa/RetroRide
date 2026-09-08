# RetroRide 🚗

> A specialized backend platform for buying and selling classic & retro cars in Azerbaijan.

[![Java](https://img.shields.io/badge/Java-21-orange?style=flat-square&logo=java)](https://www.java.com)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.0.6-brightgreen?style=flat-square&logo=spring)](https://spring.io/projects/spring-boot)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-blue?style=flat-square&logo=postgresql)](https://www.postgresql.org)
[![AWS](https://img.shields.io/badge/AWS-S3%20%7C%20EC2-yellow?style=flat-square&logo=amazonaws)](https://aws.amazon.com)
[![Docker](https://img.shields.io/badge/Docker-Compose-2496ED?style=flat-square&logo=docker)](https://www.docker.com)

---

## Overview

RetroRide is a RESTful backend service that connects classic car sellers and buyers. Users can register, post listings with multiple images, browse and filter cars, and manage their own ads through a secure JWT-authenticated API.

**Live server:** `http://13.53.177.40`

---

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 21 |
| Framework | Spring Boot 4.0.6 |
| Security | Spring Security + JWT (jjwt 0.11.5) |
| Database | PostgreSQL 16 + Hibernate JPA |
| Image Storage | AWS S3 (`eu-north-1`) |
| Server | AWS EC2 (Ubuntu 22.04) |
| Reverse Proxy | Nginx |
| Containerization | Docker + Docker Compose |
| Build Tool | Gradle |
| Utilities | Lombok |

---

## Project Structure

```
src/main/java/az/myapp/retroride/
├── config/
│   ├── JwtUtil.java              # JWT token generation & validation
│   ├── JwtAuthFilter.java        # Request filter for JWT
│   ├── SecurityConfig.java       # Spring Security configuration
│   ├── CustomUserDetailsService.java
│   └── S3Config.java             # AWS S3 client bean
├── controller/
│   ├── AuthController.java       # /api/auth/**
│   ├── CarController.java        # /api/cars/**
│   ├── CarImageController.java   # /api/cars/{id}/images, /api/images/**
│   └── UserController.java       # /api/users/**
├── dao/
│   ├── entity/
│   │   ├── User.java
│   │   ├── Car.java
│   │   └── CarImage.java
│   └── repository/
│       ├── UserRepository.java
│       ├── CarRepository.java
│       └── CarImageRepository.java
├── dto/
│   ├── request/
│   │   ├── RegisterRequestDto.java
│   │   ├── LoginRequestDto.java
│   │   └── CarRequestDto.java
│   └── response/
│       ├── AuthResponseDto.java
│       ├── CarResponseDto.java
│       └── UserResponseDto.java
└── service/
    ├── AuthService.java
    ├── CarService.java
    ├── CarImageService.java
    └── UserService.java
```

---

## API Endpoints

### Auth — `/api/auth`

| Method | Endpoint | Auth | Description |
|---|---|---|---|
| POST | `/api/auth/register` | ❌ | Register a new user |
| POST | `/api/auth/login` | ❌ | Login and receive JWT token |

**Register request:**
```json
{
  "name": "Nihad Musayev",
  "email": "nihad@example.com",
  "phone": "+994501234567",
  "password": "secret123"
}
```

**Login request:**
```json
{
  "email": "nihad@example.com",
  "password": "secret123"
}
```

**Response (both):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9..."
}
```

---

### Cars — `/api/cars`

| Method | Endpoint | Auth | Description |
|---|---|---|---|
| GET | `/api/cars` | ❌ | Get all active listings |
| GET | `/api/cars/{id}` | ❌ | Get a single car by ID |
| GET | `/api/cars/my` | ✅ | Get authenticated user's listings |
| POST | `/api/cars` | ✅ | Create a new listing |
| PUT | `/api/cars/{id}` | ✅ | Update a listing (owner only) |
| DELETE | `/api/cars/{id}` | ✅ | Delete a listing (owner only) |

**Create/Update request body:**
```json
{
  "brand": "Ford",
  "model": "Mustang",
  "year": 1968,
  "price": 45000,
  "mileage": 87000,
  "color": "Blue",
  "description": "Original 390 V8, fully restored."
}
```

---

### Images — `/api/cars/{carId}/images` & `/api/images`

| Method | Endpoint | Auth | Description |
|---|---|---|---|
| POST | `/api/cars/{carId}/images` | ✅ | Upload image for a car (multipart/form-data) |
| GET | `/api/cars/{carId}/images` | ❌ | Get all images for a car |
| DELETE | `/api/images/{imageId}` | ✅ | Delete an image by ID |
| GET | `/api/images/{fileName}` | ❌ | Serve image file directly |

**Upload:** `multipart/form-data`, field name: `file`

**Get images response:**
```json
[
  { "id": 1, "url": "/api/images/uuid-filename.jpg" },
  { "id": 2, "url": "/api/images/uuid-filename2.jpg" }
]
```

---

### Users — `/api/users`

| Method | Endpoint | Auth | Description |
|---|---|---|---|
| GET | `/api/users/me` | ✅ | Get current user's profile |
| PUT | `/api/users/me` | ✅ | Update name and phone |
| GET | `/api/users` | ✅ | Get all users |

**Update profile request:**
```json
{
  "name": "Nihad Musayev",
  "phone": "+994501234567"
}
```

---

## Running Locally

### Prerequisites

- Java 21
- Docker & Docker Compose
- AWS credentials (S3 bucket)

### 1. Clone the repository

```bash
git clone https://github.com/nihadmusa/retroride.git
cd retroride
```

### 2. Configure environment

Set the following environment variables (or update `application.yaml`):

```env
DB_URL=jdbc:postgresql://localhost:5432/postgres
DB_USERNAME=admin
DB_PASSWORD=yourpassword
JWT_SECRET=your-secret-key-at-least-256-bits
AWS_ACCESS_KEY=your-access-key
AWS_SECRET_KEY=your-secret-key
AWS_REGION=eu-north-1
AWS_BUCKET=your-bucket-name
```

### 3. Build the JAR

```bash
./gradlew bootJar
```

### 4. Run with Docker Compose

```bash
docker compose up -d
```

Services started:
- `postgres-db` — PostgreSQL on port `5432`
- `spring-app` — Spring Boot on port `8080`
- `nginx-server` — Nginx on port `80` (reverse proxy)

---

## Deployment

The application is deployed on **AWS EC2** (Ubuntu 22.04, `eu-north-1`).

```
User → Nginx (port 80) → Spring Boot (port 8080) → PostgreSQL (port 5432)
                                    ↓
                              AWS S3 (images)
```

To deploy updates:

```bash
# 1. Build new JAR locally
./gradlew bootJar

# 2. Copy to server
scp -i key.pem build/libs/retroride-0.0.1-SNAPSHOT.jar ubuntu@13.53.177.40:~/app.jar

# 3. Restart on server
ssh -i key.pem ubuntu@13.53.177.40
docker compose down && docker compose up -d --build
```

---

## Authentication

All protected endpoints require a `Bearer` token in the `Authorization` header:

```
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```

Tokens are valid for **7 days** (`JWT_EXPIRATION: 604800000` ms).

---

## Author

**Nihad Musayev** — [github.com/nihadmusa](https://github.com/nihadmusa) · [LinkedIn](https://linkedin.com/in/nihad-musayev-766482393)
