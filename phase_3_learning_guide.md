# Phase 3: Learning Guide - Fraud Detection Logic

In this phase, we built the **"Brain"** of our system. While the Transaction service is the "hands" that take the money, the **Fraud Detection Service** is the "mind" that decides if the money is safe or stolen.

---

## 1. Why is this Service Critical? (Importance)
In a real-world bank, millions of transactions happen every hour. Humans cannot check every single one. 
- **Efficiency**: This service analyzes a transaction in milliseconds.
- **Protection**: It catches fraud *before* the money is permanently gone.
- **Asynchronous Power**: Because it uses Kafka, it doesn't slow down the user's experience at the checkout counter.

---

## 2. How the Service is "Provided" in the Project
This service is a **Background Worker**. It does not have a website or a UI you can visit. Instead, it "lives" on the Kafka bus.
- It "provides" its service by **Listening** to a topic (`transactions`) and **Publishing** to another topic (`fraud-alerts`).
- It is horizontally scalable: If you have too many transactions, you can run 10 copies of this service, and Kafka will split the work between them automatically.

---

## 3. Commands Used in Phase 3
If you were building this using the CLI, you would use these commands to add the necessary "superpowers" (extensions) to the service:

```bash
# To allow the service to listen and talk to Kafka
quarkus extension add smallrye-reactive-messaging-kafka

# To allow the service to log information to the console
quarkus extension add logging-jboss
```

---

## 4. Why these Files? (Purpose of each file)

| File | Purpose | Why do we need it? |
| :--- | :--- | :--- |
| `Transaction.java` | **The Data Model** | It defines the "Schema". The service needs to know exactly what fields (Amount, CardNumber) it is looking at. |
| `FraudDetectionEngine.java` | **The Logic Center** | This is the "Rulebook". We separate the rules from the Kafka code so we can test the rules easily without needing Kafka. |
| `TransactionConsumer.java` | **The Kafka Bridge** | This is the "Entry/Exit" point. It connects the service to the outside world (Kafka). |
| `TransactionDeserializer.java`| **The Translator** | Kafka sends "1s and 0s" (bytes). This file translates those bytes back into a Java `Transaction` object. |
| `application.properties` | **The Configuration** | It tells the service where the Database is and which "Radio Station" (Kafka Topic) to listen to. |

---

## 5. Fraud Detection Service: The "Brain"
This service doesn't have a "Face" (REST API) yet; instead, it has "Ears" (Kafka Listener).

### Key Concepts:
- **Kafka Consumer**: Unlike the Transaction Service (which sends messages), this service **receives** them. It sits and waits for a message to arrive on the `transactions` topic.
- **Rule-based Logic**: These are "If-Then" statements. (e.g., *If* amount > $10,000, *Then* it is risky).
- **Heuristic ML**: We implemented a simple mathematical formula to simulate an AI model. In production, this is where you would call a real Machine Learning model.
- **Event Chaining**: This service consumes from one topic (`transactions`) and automatically sends the result to another topic (`fraud-alerts`).

---

## 6. Deep Dive: Line-by-Line Code Explanation

### A. The Listener (`TransactionConsumer.java`)
```java
@Incoming("transactions-in") // 1. Connect to the Kafka "Inbox". Listen for new transactions.
@Outgoing("fraud-alerts")   // 2. Connect to the Kafka "Outbox". Send the result here.
@Transactional              // 3. If the database save fails, don't send the Kafka message (all-or-nothing).
public Transaction process(Transaction transaction) {
    LOG.info("Analysis started..."); // Log for debugging.
    
    // 4. Calculate the score using our Rulebook (Engine).
    double score = engine.calculateRiskScore(transaction); 
    transaction.riskScore = score;
    
    // 5. If the score is high (above 0.7), flag it as fraud.
    if (score > 0.7) {
        transaction.status = "FRAUD_FLAGGED";
    }

    transaction.persist(); // 6. Save the result to the Database so we have a permanent record.
    
    return transaction; // 7. The returned object is automatically sent to "fraud-alerts" topic.
}
```

### B. The Engine (`FraudDetectionEngine.java`)
```java
public Double calculateRiskScore(Transaction transaction) {
    double score = 0.0;

    // Rule 1: High Amount Check
    // Transactions over 10,000 are rare and risky.
    if (transaction.amount > 10000) {
        score += 0.4;
    }

    // Rule 2: Suspicious Merchant Check
    // If the shop name is "SuspectShop", increase the risk.
    if ("SuspectShop".equalsIgnoreCase(transaction.merchant)) {
        score += 0.5;
    }

    return Math.min(score, 1.0); // Never let the score go above 1.0 (100%).
}
```

---

## 7. Key Definitions (The "Glossary")

| Term | Simple Definition |
| :--- | :--- |
| **Microprofile Reactive Messaging** | The technology that allows Quarkus to talk to Kafka using simple annotations like `@Incoming`. |
| **Asynchronous** | Doing work in the background without making the user wait. |
| **Decoupling** | Making sure services don't depend on each other. The Fraud service doesn't care *who* sent the transaction. |
| **Topic** | A "Folder" or "Channel" in Kafka where messages are stored. |
| **Consumer** | A service that "Listens" for messages on a bus (Kafka). |
| **Incoming / Outgoing** | Annotations that connect your Java methods to Kafka channels. |
| **Deserializer** | Converts a JSON string back into a Java Object (The opposite of Serializer). |
| **Heuristic** | A "Rule of Thumb" or simple formula used to solve a problem quickly. |
| **Logging** | Printing messages to the console so developers can see what the code is doing. |

---

## 8. The "Big Picture" Flow (Step-by-Step)
1. **Trigger**: The Transaction Service sends a message to the `transactions` topic.
2. **Detection**: The **Fraud Service** "hears" the message.
3. **Translation**: The **Deserializer** turns the message into a Java Object.
4. **Analysis**: The **Engine** applies the rules (Amount check, Merchant check).
5. **Persistence**: The result (Risk Score) is saved in the **PostgreSQL** database.
6. **Alert**: The analyzed transaction is sent to the `fraud-alerts` topic for the next service to handle.

---

## 9. Learning Checklist
- [ ] Do you understand why we separate the `Engine` from the `Consumer`? (Separation of Concerns).
- [ ] What is the "Outbox" of this service? (The `fraud-alerts` topic).
- [ ] Why is `@Transactional` important here? (To prevent data loss between DB and Kafka).
- [ ] Do you understand the difference between `@Incoming` and `@Outgoing`?
- [ ] Why do we need a `Deserializer`? (Kafka only sends bytes; we need Java objects).

---
**Next Goal:** In Phase 4, we will add the **AI Explanation**! We will use **LangChain4j** to ask an LLM (Gemini/Ollama) to explain *why* the engine gave it that specific risk score.
