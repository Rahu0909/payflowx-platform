# Event-Driven Architecture

## Project

**PayFlowX – Enterprise Payment Orchestration Platform**

Version: 1.0

---

# 1. Overview

PayFlowX follows an event-driven architecture to enable loose coupling between microservices. Business events are published to RabbitMQ exchanges and consumed asynchronously by interested services.

This communication model improves scalability, resilience, and service independence while reducing direct dependencies between business domains.

---

# 2. Event Bus

RabbitMQ serves as the central messaging backbone of the platform.

Responsibilities include:

- Event distribution
- Asynchronous communication
- Retry handling
- Dead Letter Queue (DLQ)
- Event durability
- Service decoupling

---

# 3. Messaging Pattern

PayFlowX uses the Publish–Subscribe pattern.

```

Publisher

↓

Exchange

↓

Routing Key

↓

Queue

↓

Consumer

```

Business services publish events without knowing which services consume them.

---

# 4. Event Publishers

| Service | Published Events |
|----------|------------------|
| Auth Service | User registration and authentication events |
| User Service | User created, KYC submitted, KYC approved, KYC rejected, account blocked, account suspended |
| Merchant Service | Merchant onboarding and approval events |
| Order Service | Order created, confirmed, cancelled, completed |
| Payment Service | Payment initiated, success, failure, cancelled |
| Settlement Service | Settlement created, completed |

---

# 5. Event Consumers

| Service | Purpose |
|----------|----------|
| Notification Service | User notifications, webhook delivery |
| Audit Service | Centralized audit logging |
| Settlement Service | Payment settlement processing |
| Payment Service | Order payment processing |

---

# 6. RabbitMQ Architecture

The platform uses Topic Exchanges to route business events.

Typical message flow:

```

Business Service

↓

RabbitMQ Exchange

↓

Routing Key

↓

Queue

↓

Consumer Service

```

Each service owns its queues and processes events independently.

---

# 7. Notification Events

Notification Service consumes business events such as:

- User Created
- KYC Submitted
- KYC Approved
- KYC Rejected
- Merchant Approved
- Order Created
- Payment Successful
- Payment Failed
- Settlement Completed

Notifications currently support:

- Event processing
- Retry queues
- Dead Letter Queues
- Webhook publishing

Planned enhancements:

- SMTP integration
- Email template engine
- Real email delivery

---

# 8. Audit Events

All business services publish audit events to the Audit Service.

Captured information includes:

- Event ID
- Correlation ID
- Aggregate ID
- Source Service
- Event Type
- Event Payload
- Timestamp

This provides centralized traceability and compliance logging.

---

# 9. Retry Strategy

Transient failures are handled using RabbitMQ retry queues.

Retry mechanism:

```

Consumer Failure

↓

Retry Queue

↓

Reprocessing

↓

Success

OR

↓

Dead Letter Queue

```

This prevents temporary failures from losing business events.

---

# 10. Dead Letter Queue (DLQ)

Failed events that exceed retry attempts are routed to a Dead Letter Queue.

Benefits:

- Prevents message loss
- Supports operational investigation
- Enables manual replay if required
- Improves platform reliability

The Notification Service exposes metrics for DLQ monitoring through Prometheus and Grafana dashboards.

---

# 11. Event Reliability

PayFlowX implements several mechanisms to ensure reliable event delivery:

- Transactional Outbox Pattern
- RabbitMQ durable messaging
- Retry queues
- Dead Letter Queues
- Idempotent event processing
- Saga-based workflow coordination

These patterns help maintain consistency across distributed services.

---

# 12. Event Ordering

Where business ordering is important, events are processed in the sequence defined by the owning service.

Examples:

Order Created

↓

Payment Initiated

↓

Payment Successful

↓

Settlement Created

↓

Settlement Completed

↓

Notification Published

↓

Audit Recorded

---

# 13. Benefits

The event-driven architecture provides:

- Loose service coupling
- Independent deployments
- Improved scalability
- Better fault isolation
- Asynchronous processing
- Faster API responses
- Easier extensibility
- Improved resilience

---

# 14. Conclusion

RabbitMQ enables PayFlowX services to communicate asynchronously while maintaining clear service boundaries. Combined with retry queues, dead-letter queues, transactional outbox, saga orchestration, and centralized auditing, the platform achieves reliable event processing suitable for modern distributed systems.