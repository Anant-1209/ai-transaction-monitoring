# Phase 2: Learning Guide - Backend Implementation

In this phase, we moved from "Infrastructure" to **"Implementation"**. We built the two most critical services: the **API Gateway** and the **Transaction Service**.

---

## 1. Commands Used in Phase 2
In this phase, I manually created the source files to ensure exact compatibility. However, if you were using the **Quarkus CLI**, you would use these commands:

```bash
# To create the Transaction Entity
# (This adds the Hibernate and Panache extensions)
quarkus extension add hibernate-orm-panache jdbc-postgresql

# To create the Kafka Emitter
quarkus extension add smallrye-reactive-messaging-kafka

# To start the dev mode (Hot Reload)
# This command runs your app and restarts it automatically when you change code!
./mvnw quarkus:dev
```

---

## 2. Transaction Service: The "Data Entry" Layer
We implemented a RESTful service that follows the **Clean Architecture** patterns.

### Key Concepts:
- **Panache Entity**: Instead of writing complex `DAO` or `Repository` classes, we use `PanacheEntity`. This allows us to do `Transaction.listAll()` or `transaction.persist()` directly on the model. This is called the **Active Record Pattern**.
- **JPA / Hibernate**: This handles the mapping between your Java object (`Transaction.java`) and the database table in PostgreSQL.
- **Reactive Messaging (Kafka Emitter)**: We injected an `Emitter<Transaction>`. When a transaction is created, we "emit" it to Kafka. This is non-blocking, meaning the service doesn't wait for Kafka to acknowledge before responding to the user.

### Why these files?
- `Transaction.java`: Defines what a transaction looks like (Amount, Merchant, etc.).
- `TransactionResource.java`: Defines the API endpoints (`GET /transactions` and `POST /transactions`).
- `application.properties`: Tells Quarkus how to connect to Postgres and which Kafka topic to use for outgoing messages.

---

## 3. API Gateway: The "Security Guard"
The Gateway is the entry point for our system. In Phase 2, we focused on **JWT (JSON Web Tokens)**.

### Key Concepts:
- **JWT Authentication**: Instead of using sessions (which are hard to manage in microservices), we use tokens. The Gateway issues a token, and other services can verify it without asking the Gateway every time.
- **`smallrye-jwt`**: This is the Quarkus library that handles the signing and verification of tokens.
- **Public/Private Keys**: 
    - The Gateway uses a **Private Key** to "sign" (create) the token.
    - Other services use the **Public Key** to "verify" the token.

### Why these files?
- `AuthResource.java`: Provides a temporary endpoint to generate a token for testing.
- `privateKey.pem` & `publicKey.pem`: The cryptographic keys needed for secure token creation.

---

## 4. Deep Dive: Line-by-Line Code Explanation

Let's break down the code so you can understand exactly what is happening.

### A. Transaction Service: The Model (`Transaction.java`)
This file defines what "Data" we are storing.

```java
@Entity // Tells the database: "Create a table for this class"
public class Transaction extends PanacheEntity { // PanacheEntity adds an ID and helper methods
    public Double amount;      // The money spent
    public String currency;    // USD, EUR, etc.
    public String merchant;    // Where was it spent? (e.g., Amazon)
    
    @Enumerated(EnumType.STRING) // Store as text ("PENDING") instead of a number
    public TransactionStatus status;
    
    public Double riskScore;   // How suspicious is this? (0 to 1)
}
```

### B. Transaction Service: The Logic (`TransactionResource.java`)
This is the "Brain" that handles incoming web requests.

```java
@Path("/transactions") // The URL path: http://localhost:8081/transactions
public class TransactionResource {

    @Channel("transactions-out") // Connects to the Kafka "transactions" topic
    Emitter<Transaction> transactionEmitter; // The "Launcher" to send data to Kafka

    @POST // Handles "Create" requests
    @Transactional // Ensures the database save is safe (all or nothing)
    public Response create(Transaction transaction) {
        transaction.persist(); // 1. Save to PostgreSQL database
        
        transactionEmitter.send(transaction); // 2. Send to Kafka for AI analysis
        
        return Response.status(201).entity(transaction).build(); // 3. Tell user "Success!"
    }
}
```

### C. API Gateway: Security (`AuthResource.java`)
This handles the "Keys" to your house.

```java
@Path("/auth")
public class AuthResource {
    @GET
    @Path("/token")
    public String generateToken() {
        return Jwt.issuer("my-app") // Who created this token?
           .upn("user@example.com") // Which user is this?
           .groups("Admin")          // What can they do?
           .sign();                 // Sign it with our Private Key so no one can fake it
    }
}
```

---

## 5. Key Definitions (The "Glossary")

| Term | Simple Definition |
| :--- | :--- |
| **REST API** | A way for the Frontend (Angular) to talk to the Backend (Java) using URLs. |
| **JSON** | A text format that looks like `{ "name": "John" }`. It's the "Language" of the web. |
| **JPA / Hibernate** | The "Translator" that converts Java code into SQL database commands. |
| **Emitter** | A "Radio Station" that broadcasts messages to Kafka. |
| **JWT (Token)** | A digital "ID Card" that proves who you are without using a password every time. |

---

## 6. The Flow (Step-by-Step)
1. **Request**: A user sends a JSON transaction to `POST /transactions`.
2. **Persistence**: The `TransactionResource` saves it to the `fraud_detection_db`.
3. **Event**: The `Emitter` sends the transaction data to the `transactions` topic in Kafka.
4. **Response**: The user gets a `201 Created` response immediately.

---

## 7. Learning Checklist
- [ ] Do you understand why we used `PanacheEntity`? (It simplifies database code).
- [ ] Why is the Kafka emitter used in the `POST` method? (To trigger the fraud analysis asynchronously).
- [ ] What is the purpose of the `.pem` files? (Security/Cryptography for JWT).

---
**Next Goal:** In Phase 3, we will build the **Fraud Detection Service**, which will "listen" to the messages we just started sending!
