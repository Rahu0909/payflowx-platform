# PayFlowX - High Level Design

## Architecture Style
Microservices Architecture

## Core Components

Client
↓
API Gateway
↓
Service Discovery (Eureka)

Services:
- Auth Service
- Merchant Service
- Order Service
- Payment Service
- Refund Service
- Ledger Service
- Notification Service
- Webhook Service

Infrastructure:
- PostgreSQL
- Redis
- RabbitMQ

Observability:
- Prometheus
- Grafana
- Zipkin

## Communication Model

### Synchronous
REST / Feign Client

Used for:
- auth validation
- merchant lookup
- order lookup

### Asynchronous
RabbitMQ

Used for:
- payment completed
- refund processed
- webhook dispatch
- notifications

## Security
- OAuth2 / JWT
- API Gateway filters
- Role based authorization

## Deployment
Dockerized independent services