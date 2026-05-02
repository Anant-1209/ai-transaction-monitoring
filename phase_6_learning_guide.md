# Phase 6: Learning Guide - Angular Dashboard

In this phase, we built the **"Face"** of our system. The **Angular Dashboard** brings everything together into a beautiful, real-time interface where you can actually see the AI working.

---

## 1. Why is the Dashboard Critical? (Importance)
A backend without a frontend is like an engine without a car.
- **Visualization**: It turns raw JSON data into charts, risk bars, and color-coded status labels.
- **Interaction**: It allows you to simulate transactions and see how the system reacts.
- **Real-time Awareness**: Through WebSockets, the dashboard "comes alive" whenever a fraud alert is detected.

---

## 2. Commands Used in Phase 6
To build this frontend, we used standard Angular CLI commands:

```bash
# To generate the Transaction Service
ng generate service services/transaction

# To generate the Notification Service
ng generate service services/notification

# To run the dashboard locally
ng serve
```

---

## 3. Why these Files? (Purpose of each file)

| File | Purpose | Why do we need it? |
| :--- | :--- | :--- |
| `transaction.model.ts` | **The Blueprint** | Ensures the Frontend and Backend use the same "Data Shape". |
| `transaction.service.ts`| **The API Connector**| Handles standard HTTP calls (GET, POST) to the Transaction Service. |
| `notification.service.ts`| **The Socket Connector**| Handles the live WebSocket connection to the Notification Service. |
| `app.component.html` | **The UI Layout** | Defines the "Look" (Table, Form, Risk Bars, Modal). |
| `styles.css` | **The Aesthetics** | Provides the premium "Dark Mode" design and animations. |

---

## 4. Deep Dive: Line-by-Line Code Explanation

### A. The WebSocket Connector (`notification.service.ts`)
```typescript
private connect() {
  // 1. Open a live "Pipe" to the Notification Service
  this.socket = new WebSocket('ws://localhost:8083/alerts');

  // 2. Whenever a message arrives in the pipe...
  this.socket.onmessage = (event) => {
    const alert = JSON.parse(event.data); // 3. Turn JSON text into a TypeScript object
    this.alertSubject.next(alert);        // 4. "Broadcast" it to anyone watching in the app
  };
}
```

### B. The Main Logic (`app.component.ts`)
```typescript
ngOnInit() {
  this.loadTransactions(); // 1. Get history from the Database
  
  // 2. Subscribe to the "Alert Stream"
  this.notificationService.getAlerts().subscribe(alert => {
    this.alerts.unshift(alert); // 3. Add the new alert to the TOP of the list
    
    // 4. Find the transaction in our list and update its AI explanation!
    const index = this.transactions.findIndex(t => t.id === alert.transactionId);
    if (index !== -1) {
      this.transactions[index].status = alert.status;
      this.transactions[index].fraudExplanation = alert.fraudExplanation;
    }
  });
}
```

---

## 5. Key Definitions (The "Glossary")

| Term | Simple Definition |
| :--- | :--- |
| **Observable** | A "Stream" of data that you can "Listen" (Subscribe) to. |
| **Dependency Injection**| Asking Angular to "Give" you a service (like `TransactionService`) instead of creating it yourself. |
| **Standalone Component**| A modern Angular feature where a component is self-contained and doesn't need a "Module" file. |
| **Directives (`*ngIf`, `*ngFor`)** | Special HTML attributes that add logic (If-statements and Loops) to your UI. |
| **Two-way Data Binding (`ngModel`)** | Syncs what the user types in a box with a variable in the JavaSript code. |

---

## 6. The "Big Picture" Flow (Phase 6 Integration)
1. **User Action**: You type "SuspectShop" and "$15,000" in the dashboard form.
2. **Post**: Angular sends this to the **API Gateway**.
3. **Internal Chain**: Backend finishes (Ingest -> Analyze -> AI -> Notify).
4. **Socket Push**: The **Notification Svc** pushes the result to the dashboard's WebSocket.
5. **UI Update**: The dashboard row for that transaction suddenly turns **RED** and the "Risk Score" bar moves to 90%.
6. **AI Modal**: You click "View AI Logic" and see Gemini's explanation!

---

## 7. Learning Checklist
- [ ] Do you understand the difference between an HTTP call (GET/POST) and a WebSocket connection?
- [ ] Why is it important to use `reverse()` or `unshift()`? (To show the NEWEST transactions at the top).
- [ ] How does the UI know to update a specific row when an alert arrives? (By finding the Transaction ID in the array).

---
**Next Goal:** In Phase 7, we will **Dockerize** everything! We will write the Dockerfiles for each service and the Angular app, so you can run the *entire* system with a single command.
