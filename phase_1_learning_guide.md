# Phase 1: Learning Guide - Architecture & Infrastructure

Welcome to the first phase of building your **AI-Powered Fraud Detection System**. This document explains the "Why" and "How" behind the project setup.

## 1. The Architectural Strategy: Microservices
Instead of one big application (Monolith), we are building 4 small, independent services.
- **Why?** If the `Notification Service` crashes, users can still submit transactions. If the `Fraud Detection` needs more power, we can scale just that service.
- **Microservices used:**
    - `api-gateway`: The "Front Desk" (Auth & Routing).
    - `transaction-service`: The "Clerk" (Data Entry).
    - `fraud-detection-service`: The "Security Guard" (AI & Rules).
    - `notification-service`: The "Messenger" (Alerts).

## 2. The Core Infrastructure (Docker Compose)
We use **Docker Compose** to run our infrastructure. This ensures that the system runs exactly the same on your machine, my machine, and the cloud.

### Components:
| Component | Purpose | Why this choice? |
| :--- | :--- | :--- |
| **PostgreSQL** | Database | Industry standard for reliable, transactional data. |
| **Apache Kafka** | Event Streaming | Allows services to be "Decoupled". One service doesn't need to know the other exists; they just "listen" to the bus. |
| **KRaft Mode** | Kafka Protocol | Newer way to run Kafka without needing Zookeeper (fewer containers to manage). |

## 3. The Backend: Quarkus (Java)
We chose **Quarkus** over Spring Boot for this project.
- **Supersonic Subatomic Java**: It has a tiny memory footprint and starts in milliseconds.
- **Developer Joy**: Features like Live Reloading and Dev Services make development much faster.
- **Cloud-Native**: Designed specifically for Docker and Kubernetes.

## 4. The Bridge to AI: LangChain4j
Instead of writing complex code to talk to LLMs, we use **LangChain4j**.
- It works like "Hibernate for AI".
- You can switch from **Gemini** to **Ollama** or **OpenAI** just by changing a line in the configuration.

## 5. Commands Used So Far
Here are the commands I executed to set up your environment:

### Root Setup
1. Created `.env` for secrets.
2. Created `docker-compose.yml` for infrastructure.

### Angular Frontend Scaffolding
```bash
npx -y @angular/cli@latest new dashboard-frontend --routing --style=css --skip-git --skip-install
```
- `-y`: Auto-accept prompts.
- `--routing`: Adds navigation support.
- `--skip-install`: Skips downloading `node_modules` immediately (saves time during scaffolding).

### Quarkus Services Scaffolding
I manually generated the `pom.xml` files for each service. In a terminal, you would typically use:
```bash
# Example for Transaction Service
mvn io.quarkus.platform:quarkus-maven-plugin:3.9.2:create \
    -DprojectGroupId=com.fraud.detection \
    -DprojectArtifactId=transaction-service \
    -Dextensions="hibernate-orm-panache,jdbc-postgresql,smallrye-reactive-messaging-kafka,resteasy-reactive-jackson"
```

## 6. The "Event-Driven" Flow
Understanding this is the most important part of Phase 1:
1. **Producer**: `transaction-service` produces a message: *"New Transaction: $500"*.
2. **Broker**: **Kafka** holds that message.
3. **Consumer**: `fraud-detection-service` consumes it, analyzes it, and produces a *"Fraud Alert"* if needed.
4. **Final Consumer**: `notification-service` consumes the alert and pushes it to the UI.

---
**Next Learning Goal:** In Phase 2, we will dive into **Java Code** and see how Quarkus uses **Panache ORM** for easy database work and **SmallRye Reactive Messaging** for Kafka.
