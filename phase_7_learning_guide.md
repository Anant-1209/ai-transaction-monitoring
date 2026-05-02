# Phase 7: Learning Guide - Dockerization & Orchestration

In this phase, we moved from "Code on my machine" to **"Production Ready"**. We used **Docker** to package all 5 microservices into portable containers that can run anywhere in the world.

---

## 1. Why is Dockerization Critical? (Importance)
- **"It works on my machine"**: This problem is gone. Docker ensures that the Java version, the operating system, and all libraries are exactly the same everywhere.
- **Isolation**: Each microservice lives in its own "bubble". If the `fraud-service` uses too much CPU, it won't crash the `database`.
- **Easy Deployment**: You can now deploy this entire system to AWS, Render, or a private server with just one command.

---

## 2. How Orchestration is Provided
We use **Docker Compose** as the "Conductor" of our orchestra.
- It reads the `.env` file for secrets.
- It creates a private network so the services can talk to each other (e.g., `transaction_svc` can find `postgres_db` just by its name).
- It manages the **Startup Order** using `depends_on`.

---

## 3. Commands Used in Phase 7
To run your production-ready system, you only need these two commands:

```bash
# To build and start everything in the background
docker-compose up --build -d

# To see the logs of all services at once
docker-compose logs -f

# To stop everything
docker-compose down
```

---

## 4. Why these Files? (Purpose of each file)

| File | Purpose | Why do we need it? |
| :--- | :--- | :--- |
| `Dockerfile` (Backend) | **The Recipe** | Tells Docker how to install Java, compile our Quarkus code, and start the app. |
| `Dockerfile` (Frontend) | **The Web Recipe** | Tells Docker how to compile Angular and serve it using a fast web server called **Nginx**. |
| `docker-compose.yml` | **The Blueprint** | Connects all the containers together and sets up the databases and Kafka. |

---

## 5. Deep Dive: Line-by-Line Code Explanation

### A. The Backend Dockerfile (Multi-Stage)
We use "Multi-Stage" builds to keep our final image small and secure.

```dockerfile
# Stage 1: The Kitchen (Build)
FROM maven:3.9.6 AS build  // 1. Use a heavy image with Maven and Java to COMPILE.
WORKDIR /app
COPY . .
RUN mvn package            // 2. Turn Java code into a runnable .jar file.

# Stage 2: The Dining Room (Run)
FROM eclipse-temurin:17-jre-alpine // 3. Use a tiny image with ONLY Java (no Maven).
COPY --from=build /app/target/...  // 4. Copy ONLY the finished .jar from the kitchen.
ENTRYPOINT ["java", "-jar", "..."] // 5. Start the app.
```

### B. The Orchestration (`docker-compose.yml`)
```yaml
transaction-service:
  build: ./transaction-service // 1. Look for the Dockerfile in this folder.
  environment:                 // 2. Pass the database passwords from .env.
    POSTGRES_USER: ${POSTGRES_USER}
  depends_on:                  // 3. Don't start this until Postgres is ready!
    - postgres
```

---

## 6. Key Definitions (The "Glossary")

| Term | Simple Definition |
| :--- | :--- |
| **Image** | A "Snapshot" of your app (like a frozen pizza). |
| **Container** | A "Running instance" of an image (like the pizza being cooked and eaten). |
| **Orchestration**| Managing many containers at once (Docker Compose). |
| **Multi-stage Build**| Building the code in one container and running it in another to save space. |
| **Nginx** | A super-fast web server we use to serve your Angular dashboard. |

---

## 7. The "Big Picture" Flow (Phase 7)
1. **Command**: You run `docker-compose up`.
2. **Build**: Docker reads 5 different `Dockerfiles` and creates 5 images.
3. **Network**: Docker creates a virtual network for them to talk.
4. **Launch**:
    - `Postgres` and `Kafka` start first.
    - `Transaction`, `Fraud`, and `Notification` services start next.
    - `Angular Dashboard` starts last.
5. **Ready**: You open `http://localhost:4200` and the whole system is live!

---

## 8. Learning Checklist
- [ ] Do you understand why we use two `FROM` lines in the Dockerfile? (Multi-stage build).
- [ ] What is the difference between a `Dockerfile` and `docker-compose.yml`?
- [ ] How do the services know the database password? (Through the `.env` file and environment variables).

---
**Next Goal:** Phase 8 & 9 - Final Deployment and Testing!
