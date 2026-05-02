# Phase 5: Learning Guide - Notification Service (Real-time Alerts)

In this final backend phase, we built the **"Loudspeaker"** of our system. The **Notification Service** is responsible for taking the AI-analyzed alerts from Kafka and pushing them instantly to the user's screen using **WebSockets**.

---

## 1. The "Big Picture" Analogy
To understand the flow of the entire project, imagine a factory:
1.  **Transaction Service (The Cashier)**: Takes an order and places it on a conveyor belt (**Kafka**).
2.  **Fraud Service (The Inspector)**: Picks the order off the belt, checks it against rules, and asks an **Expert Analyst (AI/Gemini)** for an explanation.
3.  **Notification Service (The Loudspeaker)**: As soon as the Inspector is done, the Loudspeaker announces the result so the Manager (**You/Dashboard**) can hear it immediately.

---

## 2. Why is this Service Critical? (Importance)
- **Real-time Reaction**: If fraud is happening right now, you can't wait for a page to refresh. You need an "Alert" to pop up instantly.
- **Bi-directional Communication**: Unlike standard web pages (where the browser asks for data), **WebSockets** allow the server to "push" data to the browser whenever it wants.
- **Decoupling**: The Notification service doesn't care *how* the fraud was detected. It only cares about "shouting" the result to the dashboard.

---

## 3. How the Service is "Provided" in the Project
This service provides a **WebSocket Server**.
- It listens to the `fraud-alerts` Kafka topic.
- It maintains a live connection with the **Angular Dashboard**.
- When a message arrives from Kafka, it "broadcasts" it to every connected dashboard.

---

## 4. Commands Used in Phase 5
To build this real-time engine, we added these Quarkus extensions:

```bash
# To allow the service to listen to Kafka alerts
quarkus extension add smallrye-reactive-messaging-kafka

# To allow the service to use WebSockets (Next Generation)
quarkus extension add websockets-next
```

---

## 5. Why these Files? (Purpose of each file)

| File | Purpose | Why do we need it? |
| :--- | :--- | :--- |
| `FraudAlert.java` | **Data Object** | A simple container for the alert data (ID, Score, AI Explanation). |
| `AlertSocket.java` | **The Loudspeaker** | This is the actual WebSocket server. It manages the connections to the Angular dashboard. |
| `AlertConsumer.java`| **The Bridge** | This listens to Kafka and "hands off" the data to the `AlertSocket`. |
| `AlertDeserializer.java`| **The Translator** | Translates Kafka bytes back into a Java `FraudAlert` object. |

---

## 6. Deep Dive: Line-by-Line Code Explanation

### A. The WebSocket Server (`AlertSocket.java`)
```java
@WebSocket(path = "/alerts") // 1. Defines the "Channel". Angular will connect to ws://localhost:8083/alerts
public class AlertSocket {

    @Inject
    WebSocketConnection connection; // 2. Represents the live connection to the dashboard.

    @OnOpen // 3. This runs when a dashboard connects.
    public void onOpen() {
        LOG.info("New dashboard connected!");
    }

    public void broadcast(FraudAlert alert) {
        // 4. Send the alert data to ALL connected users at the same time.
        connection.broadcast().sendTextAndAwait(alert);
    }
}
```

### B. The Kafka Listener (`AlertConsumer.java`)
```java
@Incoming("fraud-alerts-in") // 1. Listen for processed alerts from the Fraud Service.
public void consume(FraudAlert alert) {
    LOG.info("Received alert from Kafka...");
    
    // 2. Pass the alert to the WebSocket "Loudspeaker".
    socket.broadcast(alert); 
    
    LOG.info("Alert pushed to UI.");
}
```

---

## 7. The Full Project Flow (Phase 1 to 5)
1.  **Frontend**: Sends a transaction.
2.  **Transaction Svc**: Saves to DB -> Sends to Kafka (`transactions`).
3.  **Fraud Svc**: Receives from Kafka -> Rules/ML check -> AI Explanation (Gemini) -> Sends to Kafka (`fraud-alerts`).
4.  **Notification Svc**: Receives from Kafka -> Pushes to WebSocket.
5.  **Frontend**: Receives WebSocket message and shows a red alert.

---

## 8. Core Definitions

| Term | Simple Definition |
| :--- | :--- |
| **WebSocket** | A persistent connection between client and server that stays open for real-time data. |
| **Broadcast** | Sending the same message to everyone who is currently connected. |
| **Persistence** | Storing data in a database so it isn't lost when the power goes out. |
| **Consumer** | A piece of code that "eats" (receives) messages from Kafka. |

---

## 9. Learning Checklist
- [ ] Do you understand why we use a WebSocket instead of a regular REST API for alerts? (Real-time speed).
- [ ] What is the "Input" of the Notification Service? (Kafka `fraud-alerts` topic).
- [ ] What is the "Output" of the Notification Service? (The WebSocket connection to the browser).

---
**Next Goal:** In Phase 6, we will build the **Angular Dashboard**! We will finally see all these backend services in action on a beautiful, real-time UI.
