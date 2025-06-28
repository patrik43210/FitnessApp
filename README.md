# 🏋️‍♂️ FitnessApp Microservices

A simple fitness application built with **Spring Boot Microservices**, **Spring Cloud Eureka**, **API Gateway**, **PostgreSQL**, **MongoDB**, **RabbitMQ**, and **Swagger UI** for easy testing.

---

## 📦 **Tech Stack**

- **Spring Boot**
- **Spring Cloud Eureka** (Service Discovery)
- **API Gateway** (Spring Cloud Gateway)
- **RabbitMQ** (Message Queue)
- **PostgreSQL** (User Service)
- **MongoDB** (Activity & AI Services)
- **Swagger** (API Documentation)

---

## 🚀 **Local Setup**

Clone the repo, then spin up your databases & message queue using Docker.

---

### ✅ PostgreSQL

```bash
docker run --name fitnessapp-postgres   -e POSTGRES_USER=YOUR_USERNAME   -e POSTGRES_PASSWORD=YOUR_PASSWORD   -e POSTGRES_DB=fitnessapp_db   -p 5432:5432   -d postgres
```

---

### ✅ MongoDB

```bash
# With authentication
docker run -d --name mongodb   -p 27017:27017   -e MONGO_INITDB_ROOT_USERNAME=YOUR_USERNAME   -e MONGO_INITDB_ROOT_PASSWORD=YOUR_PASSWORD   mongo:latest

# Or without authentication
docker run -d --name mongodb   -p 27017:27017   mongo:latest
```

---

### ✅ RabbitMQ

```bash
docker run -it --rm --name rabbitmq   -p 5672:5672 -p 15672:15672   rabbitmq:4-management
```

or

```bash
docker run -it --name rabbitmq   -p 5672:5672 -p 15672:15672   rabbitmq:4-management
```

📌 **RabbitMQ Management UI:** [http://localhost:15672](http://localhost:15672) (default user: `guest` / `guest`)

---

## 🗂️ **Services**

| Service              | Port   | Swagger UI                                                |
| -------------------- | ------ | --------------------------------------------------------- |
| **Eureka Server**    | `8761` | [http://localhost:8761](http://localhost:8761)            |
| **API Gateway**      | `8080` | -                                                         |
| **User Service**     | `8081` | [Swagger UI](http://localhost:8081/swagger-ui/index.html) |
| **Activity Service** | `8082` | [Swagger UI](http://localhost:8082/swagger-ui/index.html) |
| **AI Service**       | `8083` | [Swagger UI](http://localhost:8083/swagger-ui/index.html) |

---

## 🔀 **API Gateway**

Routes:

- `/api/users/**` → `user-service`
- `/api/activities/**` → `activity-service`
- `/api/recommendations/**` → `ai-service`

Make sure all services are **registered in Eureka**.

---

## 📌 **Endpoints**

### 👤 **User Service**

**GET**

- `/api/users/{userId}`
- `/api/users/allUsers`
- `/api/users/{userId}/validate`

**POST**

- `/api/users/register`

Example request:

```json
{
  "email": "abc@gmail.com",
  "password": "abv@123",
  "firstName": "Pesho",
  "lastName": "Ivanov"
}
```

---

### 🏃‍♂️ **Activity Service**

**GET**

- `/api/activities/allActivities`
- `/api/activities/user-activities` — with `X-User-ID` header
- `/api/activities/{activityId}`

---

### 🤖 **AI Service**

**GET**

- `/api/recommendations/user/{userId}`
- `/api/recommendations/activity/{activityId}`

---

## ✅ **How to Run**

1️⃣ **Start containers**\
2️⃣ **Run Eureka Server**\
3️⃣ **Run Config Server (if used)**\
4️⃣ **Run API Gateway**\
5️⃣ **Run all microservices**

Swagger UI is available for quick API testing!

---

**Author:** [Patrik Ivanov]\
