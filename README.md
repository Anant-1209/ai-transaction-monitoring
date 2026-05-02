# Real-time Transaction Monitoring & Fraud Detection

This project is a production-ready microservices system for monitoring transactions and detecting fraud using AI.

## Tech Stack
- **Backend**: Quarkus (Java)
- **Frontend**: Angular
- **AI**: LangChain4j (Gemini / Ollama)
- **Data**: PostgreSQL
- **Streaming**: Apache Kafka
- **DevOps**: Docker & Docker Compose

## Project Structure
- `api-gateway/`: Central entry point and security (JWT).
- `transaction-service/`: Manages transaction ingestion and storage.
- `fraud-detection-service/`: Core logic for fraud scoring and AI explanations.
- `notification-service/`: Real-time alerts via WebSockets.
- `dashboard-frontend/`: Angular-based visualization.
- `docker-compose.yml`: Infrastructure orchestration.

## Getting Started
1. Update `.env` with your `GEMINI_API_KEY`.
2. Run `docker-compose up -d` to start the infrastructure.
3. Follow the phase-by-phase implementation guide.
