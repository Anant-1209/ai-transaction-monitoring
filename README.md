# Guardia AI | Real-time Transaction Monitoring & Fraud Detection

![Quarkus](https://img.shields.io/badge/Quarkus-ffbb00?style=for-the-badge&logo=quarkus&logoColor=white)
![Angular](https://img.shields.io/badge/Angular-DD0031?style=for-the-badge&logo=angular&logoColor=white)
![Apache Kafka](https://img.shields.io/badge/Apache%20Kafka-000000?style=for-the-badge&logo=apachekafka&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-316192?style=for-the-badge&logo=postgresql&logoColor=white)
![Google Gemini](https://img.shields.io/badge/Google%20Gemini-8E75B2?style=for-the-badge&logo=googlegemini&logoColor=white)
![Docker](https://img.shields.io/badge/docker-%230db7ed.svg?style=for-the-badge&logo=docker&logoColor=white)

This project is a production-ready microservices system for monitoring transactions and detecting fraud using AI.

## Tech Stack
- **Backend**: Quarkus (Java)
- **Frontend**: Angular
- **AI**: LangChain4j (Google Gemini / Ollama)
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
