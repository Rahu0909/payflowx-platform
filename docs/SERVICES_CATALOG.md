# Service Catalog

## Project

**PayFlowX – Enterprise Payment Orchestration Platform**

Version: 1.0

---

# Overview

PayFlowX is built using a domain-driven microservices architecture. Each service owns its business capability, database, APIs, events, and operational metrics while collaborating through synchronous REST communication and asynchronous RabbitMQ messaging.

The platform currently consists of **10 independently deployable services**.

---

# Platform Services

| Service | Primary Responsibility | Database | RabbitMQ | Public APIs |
|----------|------------------------|----------|----------|-------------|
| Discovery Server | Service registry | ❌ | ❌ | ❌ |
| API Gateway | Request routing, authentication, rate limiting | ❌ | ❌ | Gateway Routes |
| Auth Service | Authentication, JWT, user registration | PostgreSQL | ✅ | REST |
| User Service | User profile, KYC, addresses | PostgreSQL | ✅ | REST |
| Merchant Service | Merchant onboarding and verification | PostgreSQL | ✅ | REST |
| Order Service | Order lifecycle management | PostgreSQL | ✅ | REST |
| Payment Service | Payment processing | PostgreSQL | ✅ | REST |
| Settlement Service | Merchant settlements | PostgreSQL | ✅ | REST |
| Notification Service | Notifications and webhooks | PostgreSQL | ✅ | Event Consumers |
| Audit Service | Centralized audit logging | PostgreSQL | ✅ | Internal |

---

# Service Details

---

## 1. Discovery Server

### Purpose

Acts as the central service registry using Netflix Eureka.

### Responsibilities

- Service registration
- Service discovery
- Health monitoring

### Database

None

### Messaging

None

### Dependencies

None

---

## 2. API Gateway

### Purpose

Single entry point for all external client requests.

### Responsibilities

- Request routing
- JWT validation
- Rate limiting
- Request forwarding
- Header propagation
- Correlation ID propagation

### Database

None

### Messaging

None

### Dependencies

- Discovery Server
- Auth Service

---

## 3. Auth Service

### Purpose

Handles authentication and identity management.

### Responsibilities

- User registration
- Login
- Password management
- Email verification
- JWT generation
- Refresh tokens
- Internal user creation

### Database

PostgreSQL

### Security

- HS256 Strategy
- RS256 Strategy
- Strategy Pattern

### Events Published

- USER_REGISTERED
- PASSWORD_RESET
- EMAIL_VERIFIED

### Events Consumed

None

### Dependencies

- RabbitMQ
- Redis
- User Service

---

## 4. User Service

### Purpose

Manages user information after authentication.

### Responsibilities

- User profile
- Address management
- KYC
- Account status
- User validation

### Database

PostgreSQL

### Events Published

- USER_CREATED
- USER_KYC_SUBMITTED
- USER_KYC_APPROVED
- USER_KYC_REJECTED
- USER_ACCOUNT_BLOCKED
- USER_ACCOUNT_SUSPENDED

### Events Consumed

Internal user creation request

### Dependencies

- RabbitMQ
- Audit Service
- Notification Service

---

## 5. Merchant Service

### Purpose

Manages merchant onboarding and lifecycle.

### Responsibilities

- Merchant onboarding
- Business verification
- Merchant approval
- Merchant profile
- Merchant status

### Database

PostgreSQL

### Events Published

- MERCHANT_CREATED
- MERCHANT_APPROVED
- MERCHANT_REJECTED
- MERCHANT_SUSPENDED

### Events Consumed

User validation events

### Dependencies

- User Service
- RabbitMQ
- Notification Service
- Audit Service

---

## 6. Order Service

### Purpose

Manages complete order lifecycle.

### Responsibilities

- Order creation
- Order validation
- Order status
- Inventory validation
- Saga orchestration

### Database

PostgreSQL

### Events Published

- ORDER_CREATED
- ORDER_CONFIRMED
- ORDER_CANCELLED
- ORDER_COMPLETED

### Events Consumed

- Payment events

### Dependencies

- User Service
- Merchant Service
- Payment Service
- RabbitMQ

---

## 7. Payment Service

### Purpose

Processes customer payments.

### Responsibilities

- Payment initiation
- Payment authorization
- Payment status
- Payment validation
- Idempotent processing
- Payment method selection

### Database

PostgreSQL

### Design Patterns

- Adapter Pattern (payment method selection)
- Strategy Pattern
- Saga Pattern
- Transactional Outbox Pattern

### Events Published

- PAYMENT_INITIATED
- PAYMENT_SUCCESS
- PAYMENT_FAILED
- PAYMENT_CANCELLED

### Events Consumed

- ORDER_CREATED

### Dependencies

- Order Service
- RabbitMQ
- Redis
- Settlement Service

---

## 8. Settlement Service

### Purpose

Generates settlements for merchants.

### Responsibilities

- Settlement creation
- Settlement processing
- Settlement status
- Merchant payouts

### Database

PostgreSQL

### Events Published

- SETTLEMENT_CREATED
- SETTLEMENT_COMPLETED

### Events Consumed

- PAYMENT_SUCCESS

### Dependencies

- Payment Service
- RabbitMQ

---

## 9. Notification Service

### Purpose

Processes asynchronous notification events.

### Responsibilities

- Event consumers
- Retry mechanism
- Dead Letter Queue handling
- Webhook publishing
- Notification metrics

### Database

PostgreSQL

### Current Capabilities

- RabbitMQ consumers
- Retry queues
- DLQ
- Webhooks

### Planned

- SMTP integration
- Email template engine
- Real email delivery

### Events Consumed

Consumes business events from all domain services.

---

## 10. Audit Service

### Purpose

Maintains centralized audit logs for all business events.

### Responsibilities

- Audit event storage
- Event history
- Correlation tracking
- Compliance logging

### Database

PostgreSQL

### Events Consumed

Consumes audit events from every business service.

### Dependencies

RabbitMQ

---

# Cross-Service Infrastructure

## Service Discovery

- Netflix Eureka

## API Gateway

- Spring Cloud Gateway

## Messaging

- RabbitMQ
- Topic Exchanges
- Dead Letter Queues
- Retry Queues

## Database

- PostgreSQL
- Database per service

## Caching

- Redis

## Observability

- Micrometer
- Prometheus
- Grafana
- Zipkin
- Alertmanager

## Logging

- Logback
- Correlation IDs
- Trace IDs
- Span IDs

## CI/CD

- GitHub Actions
- Jenkins
- Docker
- GitHub Container Registry

## Deployment

- Docker Compose
- Kubernetes
- ConfigMaps
- Secrets
- HPA
- Ingress

---

# Design Principles

The platform follows several enterprise architecture principles:

- Domain-driven service decomposition
- Database per service
- Event-driven communication
- Saga Pattern
- Transactional Outbox Pattern
- Adapter Pattern
- Strategy Pattern
- Circuit Breaker Pattern
- API Gateway Pattern
- Idempotent request processing
- Distributed tracing
- Production-grade observability

---

# Service Dependency Overview

```
Client
    │
    ▼
API Gateway
    │
    ▼
Auth Service
    │
    ▼
User Service
    │
    ├──────────────► Merchant Service
    │
    ├──────────────► Order Service
    │                    │
    │                    ▼
    │              Payment Service
    │                    │
    │                    ▼
    │             Settlement Service
    │
    └──────────────► RabbitMQ
                           │
                 ┌─────────┴─────────┐
                 ▼                   ▼
        Notification Service   Audit Service
```

---

# Conclusion

PayFlowX is composed of ten independent microservices that collectively implement a secure, event-driven payment orchestration platform. Each service has clear ownership of its business domain, communicates through well-defined APIs and events, and can be deployed, scaled, and monitored independently.