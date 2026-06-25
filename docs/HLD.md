# High Level Design (HLD)

## Project

PayFlowX – Enterprise Payment Processing Platform

---

## Document Purpose
This document describes the high-level architecture, system design, service boundaries, communication patterns, infrastructure, deployment model, security model, scalability strategy, and architectural decisions of the PayFlowX platform.
The HLD serves as the primary architectural reference for developers, reviewers, architects, DevOps engineers, and interview discussions. It provides a technology-agnostic overview of how the platform is structured while documenting the reasoning behind major architectural decisions.
Unlike implementation documentation, this document focuses on architectural responsibilities, interactions between services, and platform-wide design principles rather than low-level code details.

---

## Intended Audience
This document is intended for:

- Software Engineers
- Backend Developers
- Solution Architects
- DevOps Engineers
- Technical Reviewers
- Interviewers
- Future Contributors

---

## Project Type
Enterprise Distributed Payment Platform

---

## Architecture Style
- Microservices Architecture
- Event-Driven Architecture
- Domain-Oriented Service Boundaries
- Database per Service
- Asynchronous Messaging
- REST-based Synchronous Communication

---

## Version
Current Version: 1.0
Document Status: Active
Last Updated: June 2026

## Revision History
| Version | Date | Author | Description |
|----------|------|--------|-------------|
| 1.0 | June 2026 | Rahul Agarwal | Initial High Level Design Document |

# Executive Summary
PayFlowX is an enterprise-grade payment processing platform designed to demonstrate modern distributed system architecture using Java and the Spring ecosystem.
The platform is composed of independently deployable microservices responsible for authentication, user management, merchant onboarding, order processing, payment execution, settlement management, notifications, and audit logging. Each service owns its business domain and persistence layer, enabling loose coupling, independent scalability, and isolated deployments.
Service-to-service communication combines synchronous REST APIs for immediate business validations with asynchronous RabbitMQ events for eventual consistency, notifications, auditing, and long-running workflows.
To ensure platform reliability, PayFlowX incorporates production-oriented architectural patterns including Saga Pattern for distributed transaction orchestration, Transactional Outbox Pattern for reliable event publication, Idempotency for duplicate request protection, Redis-based caching, Resilience4j for fault tolerance, centralized audit logging, distributed tracing, and comprehensive observability using Prometheus, Grafana, and Zipkin.
The platform follows a security-first approach through JWT-based authentication, role-based authorization, gateway-centric request routing, and centralized token validation. Authentication supports both HS256 and RS256 signing algorithms through the Strategy Pattern, allowing cryptographic algorithms to be selected without modifying business logic.
Operationally, the platform is designed for cloud-native deployment. Every service is containerized using Docker, orchestrated through Kubernetes manifests, continuously built using GitHub Actions and Jenkins, and monitored through a centralized observability stack.
Although PayFlowX is a portfolio project, its architecture intentionally mirrors engineering practices commonly found in modern payment platforms, emphasizing scalability, maintainability, resilience, and operational visibility over monolithic application design.

# Business Problem
Modern payment platforms must process thousands of concurrent financial operations while maintaining consistency, reliability, auditability, and security.
Traditional monolithic architectures often become difficult to scale as payment domains such as authentication, merchant onboarding, payment processing, settlement, notifications, and auditing evolve independently. Tight coupling between these domains increases deployment risk, limits scalability, and makes fault isolation difficult.
Financial systems also require strong guarantees around transaction processing, duplicate request handling, event consistency, and regulatory audit trails. Simple CRUD-based architectures are insufficient for handling these requirements at scale.
PayFlowX addresses these challenges by decomposing the payment platform into domain-focused microservices. Each service independently manages its business capability while collaborating through synchronous APIs and asynchronous domain events.
The architecture prioritizes service isolation, fault tolerance, eventual consistency, observability, and operational transparency, enabling the platform to evolve without introducing unnecessary coupling between business domains.

# Vision
The vision of PayFlowX is to provide a modular, scalable, and production-oriented payment processing platform that demonstrates enterprise backend engineering principles.
Rather than focusing solely on payment execution, the platform aims to model the broader ecosystem surrounding digital payments, including identity management, merchant lifecycle, order orchestration, settlement workflows, notifications, auditing, observability, and operational automation.
The long-term architectural direction emphasizes cloud-native deployment, resilient distributed systems, event-driven communication, and maintainable service boundaries that can evolve independently while preserving overall platform consistency.

# Objectives
The primary objectives of the platform are:

- Build a fully distributed payment processing platform using Spring Boot microservices.
- Demonstrate production-grade architectural patterns suitable for financial systems.
- Maintain clear ownership boundaries between business domains.
- Ensure reliable event delivery using asynchronous messaging.
- Support secure authentication and authorization across services.
- Provide complete auditability of business-critical operations.
- Enable independent deployment and horizontal scaling of services.
- Implement comprehensive monitoring, tracing, and operational visibility.
- Automate build, packaging, and deployment pipelines using modern DevOps practices.
- Produce an architecture that accurately reflects real-world enterprise payment systems.

# Scope
The current implementation includes:

- Authentication Service
- User Service
- Merchant Service
- Order Service
- Payment Service
- Settlement Service
- Notification Service
- Audit Service
- API Gateway
- Discovery Server

Supporting platform capabilities include:

- JWT Authentication
- RabbitMQ Messaging
- Saga Coordination
- Transactional Outbox
- Redis Integration
- Resilience4j
- Idempotency
- Docker Deployment
- Kubernetes Deployment Assets
- GitHub Actions CI
- Jenkins Pipeline
- Prometheus Monitoring
- Grafana Dashboards
- Zipkin Distributed Tracing
- AlertManager
- Swagger/OpenAPI Documentation

# Stakeholders
Primary stakeholders include:

- Platform Users
- Platform Administrators
- Merchant Onboarding Team
- Operations Team
- Backend Engineering Team
- DevOps Team
- System Architects
- Project Maintainers

# 9. Functional Requirements
PayFlowX provides a complete payment processing ecosystem composed of multiple independently deployable microservices. The platform supports user onboarding, merchant enrollment, payment processing, settlement, notifications, auditing, and operational monitoring.
The major functional capabilities of the platform are described below.

## Authentication
- User registration
- User login
- JWT access token generation
- Refresh token support
- RS256 and HS256 JWT signing using Strategy Pattern
- Role-based authentication (USER, ADMIN)
- Secure password hashing
- Token validation for downstream services

---

## User Management
- Automatic user profile creation after successful registration
- Profile management
- Address management
- Default address selection
- KYC submission
- KYC approval and rejection
- User status management
- User eligibility validation for payments

---

## Merchant Management
- Merchant onboarding
- Business verification
- Merchant approval workflow
- Merchant rejection workflow
- Merchant profile management
- Merchant activation and suspension
- Merchant eligibility validation

---

## Order Management
- Order creation
- Order lifecycle management
- Order status tracking
- Order validation
- Order cancellation
- Order history retrieval

---

## Payment Processing
- Payment initiation
- Payment authorization
- Payment execution
- Payment status tracking
- Idempotent payment processing
- Multiple payment method support

Supported payment adapters include:

- Card
- UPI
- Net Banking

Payment method selection is implemented using the Adapter Pattern, allowing different payment processors to be selected dynamically based on the customer's chosen payment method.

---

## Settlement
- Settlement creation
- Settlement processing
- Settlement completion
- Settlement history
- Merchant settlement records

---

## Notification
- Event-driven notifications
- Email notification events
- RabbitMQ consumers
- Dead Letter Queue (DLQ) handling
- Retry mechanism
- Notification history

(Note: Real SMTP email delivery and template engine integration are planned but are not part of the current implementation.)

---

## Audit
- Centralized audit logging
- Business event persistence
- Audit event querying
- Cross-service event tracking
- Correlation ID propagation

---

## API Gateway
- Request routing
- Authentication forwarding
- JWT validation
- Header propagation
- Service discovery integration

---

## Platform Observability
- Prometheus metrics
- Grafana dashboards
- Zipkin distributed tracing
- Health endpoints
- Custom business metrics
- Centralized logging

---

## DevOps
- Docker containerization
- Kubernetes deployment manifests
- GitHub Actions CI pipeline
- Jenkins build pipeline
- SonarQube integration

# 10. Non-Functional Requirements
The PayFlowX platform has been designed with production-oriented quality attributes that extend beyond functional business capabilities.

## Scalability
- Independently scalable microservices
- Stateless service design
- Kubernetes-ready deployment model
- Horizontal Pod Autoscaler (HPA) support
- Database-per-service architecture

---

## Availability
- Service isolation
- Independent deployments
- Event-driven communication
- RabbitMQ retry mechanisms
- Dead Letter Queue support

---

## Reliability
- Saga Pattern for distributed transactions
- Transactional Outbox Pattern
- Idempotency framework
- Retry mechanisms
- Persistent audit logging

---

## Performance
- Redis caching
- Optimized JPA queries
- EntityGraph usage where appropriate
- Asynchronous event processing
- Lightweight REST communication

---

## Security
- JWT Authentication
- HS256 Strategy
- RS256 Strategy
- Strategy Pattern for signing algorithm selection
- Gateway-based authentication
- Role-based authorization
- Password encryption
- Secure header propagation

---

## Maintainability
- Domain-driven service boundaries
- Layered architecture
- Common exception handling
- Centralized constants
- DTO-based API contracts
- MapStruct mapping
- Lombok code reduction

---

## Observability
- Distributed tracing
- Correlation IDs
- Prometheus metrics
- Grafana dashboards
- Zipkin tracing
- Structured Logback logging

---

## Fault Tolerance
- Resilience4j Circuit Breakers
- Retry policies
- Graceful degradation
- RabbitMQ DLQ
- Timeout handling

---

## Consistency
- Eventual consistency across services
- Saga orchestration
- Reliable event publishing through Transactional Outbox

---

## Auditability
Every critical business event generated within the platform is persisted by the Audit Service.

Examples include:

- User registration
- Merchant onboarding
- Payment execution
- Settlement processing
- Notification events

Each audit event carries:

- Event ID
- Correlation ID
- Aggregate ID
- Event Type
- Source Service
- Timestamp
- 
This enables complete end-to-end business traceability across the platform.

---

## Deployment
The platform supports:

- Local development using Docker Compose
- Kubernetes deployment
- GitHub Actions CI
- Jenkins pipeline execution
- Containerized service deployment

# 11. Architecture Principles
The architecture of PayFlowX has been designed around modern enterprise software engineering principles commonly adopted in large-scale distributed payment platforms. The primary objective is to build a system that is modular, scalable, resilient, maintainable, and operationally observable while keeping each business domain independently deployable.
The following architectural principles guide the implementation of the platform.

---

## Microservices First
The platform follows a microservices architecture where each business capability is implemented as an independent service with its own database, business logic, deployment lifecycle, and runtime configuration.
Current services include:

- Discovery Server
- API Gateway
- Auth Service
- User Service
- Merchant Service
- Order Service
- Payment Service
- Settlement Service
- Notification Service
- Audit Service
This separation enables each service to evolve independently without affecting unrelated domains.

---

## Domain Ownership
Every microservice owns a single business domain.
Examples include:
- Authentication belongs exclusively to Auth Service.
- User profile and KYC management belong to User Service.
- Merchant onboarding belongs to Merchant Service.
- Payment execution belongs to Payment Service.
- Settlement processing belongs to Settlement Service.
- Notifications belong to Notification Service.
- Audit persistence belongs to Audit Service.
Business rules are never duplicated across services.

---

## Database per Service
Every service owns its own database schema.
No service directly accesses another service's database.
All cross-service communication occurs through REST APIs or asynchronous RabbitMQ events.
This approach ensures loose coupling, service autonomy, and independent scalability.

---

## API First Design
Every service exposes well-defined REST APIs.
Public APIs are documented using OpenAPI (Swagger), enabling consistent integration between services and simplifying future client development.

---

## Event-Driven Communication
Business events are propagated asynchronously using RabbitMQ.
Examples include:

- User Created
- Merchant Approved
- Payment Completed
- Settlement Completed
- Notification Events
- Audit Events

Asynchronous communication minimizes coupling between services while improving scalability and resilience.

---

## Eventual Consistency
Instead of distributed database transactions, PayFlowX embraces eventual consistency.
Long-running business operations are coordinated through asynchronous messaging and Saga orchestration.
This approach avoids two-phase commit (2PC) while maintaining business consistency across multiple services.

---

## Stateless Services
Business services are stateless.
Application instances do not store user session information in memory.
Authentication is performed using JWTs, allowing requests to be processed by any available service instance.
This enables horizontal scaling without session affinity.

---

## Security by Design
Security considerations are incorporated throughout the architecture.
The platform implements:

- JWT authentication
- Role-based authorization
- HS256 and RS256 signing algorithms
- Strategy Pattern for JWT algorithm selection
- Secure gateway routing
- Header propagation
- Correlation ID propagation

Sensitive business operations require authenticated requests before execution.

---

## Resilience
The platform is designed to tolerate partial failures.
Fault tolerance is achieved using:

- Resilience4j Circuit Breakers
- Retry mechanisms
- RabbitMQ Dead Letter Queues
- Idempotent request processing
- Graceful degradation

Transient failures do not unnecessarily impact the entire platform.

---

## Observability
Every service exposes operational metrics and tracing information.
The observability stack includes:

- Prometheus
- Grafana
- Zipkin
- Structured Logback logging
- Correlation IDs
- Health endpoints
- Business metrics
- 
This enables rapid diagnosis of production issues and end-to-end request tracing.

---

## Cloud-Native Deployment
The platform is designed for containerized deployment.
Infrastructure supports:

- Docker
- Docker Compose
- Kubernetes
- GitHub Actions
- Jenkins

This allows consistent deployments across development, testing, and production environments.

# 12. High-Level Architecture
PayFlowX is organized as a layered distributed platform where external clients interact with the system through a centralized API Gateway.
The gateway performs request routing, authentication forwarding, and service discovery before forwarding requests to the appropriate backend service.
Business services communicate synchronously through REST APIs whenever an immediate response is required and asynchronously through RabbitMQ when eventual consistency or event propagation is sufficient.
Each service owns its own persistence layer, preventing direct database sharing and ensuring service independence.
The Discovery Server provides runtime service registration and discovery, allowing services to locate each other dynamically without hardcoded network addresses.
Supporting infrastructure such as Redis, Prometheus, Grafana, Zipkin, RabbitMQ, and PostgreSQL provide caching, monitoring, messaging, tracing, and persistence capabilities.
The overall architecture can be divided into the following layers:

---

## Client Layer
Responsible for initiating requests.
Examples include:

- Web applications
- Mobile applications
- Future third-party integrations

---

## API Layer
The API Gateway acts as the single entry point for external requests.
Responsibilities include:

- Request routing
- JWT validation
- Authentication forwarding
- Header propagation
- Service discovery integration

---

## Business Layer
Business capabilities are implemented by domain-specific microservices.
Each service encapsulates its own business rules and persistence layer.

---

## Messaging Layer
RabbitMQ provides asynchronous communication between services.
Messaging is primarily used for:

- Notifications
- Audit logging
- Saga workflows
- Domain event propagation

---

## Data Layer
Each microservice owns an independent PostgreSQL database.
No shared database exists between services.

---

## Observability Layer
Platform health is monitored through:

- Prometheus
- Grafana
- Zipkin
- Actuator endpoints
- Structured logging

---

## Infrastructure Layer
Deployment infrastructure includes:

- Docker
- Docker Compose
- Kubernetes
- GitHub Actions
- Jenkins

# 13. Platform Components
The PayFlowX platform consists of infrastructure components and business services that collectively implement the payment processing ecosystem.

## Discovery Server
Provides service registration and service discovery using Netflix Eureka.
Responsibilities:

- Service registration
- Service discovery
- Dynamic endpoint resolution

---

## API Gateway
Acts as the centralized entry point into the platform.
Responsibilities:

- Route requests
- Validate JWT tokens
- Forward authentication headers
- Integrate with Eureka

---

## Auth Service
Responsible for authentication and identity management.
Responsibilities:

- Registration
- Login
- JWT generation
- Refresh tokens
- Password encryption
- JWT validation

Supports both HS256 and RS256 signing algorithms using the Strategy Pattern.

---

## User Service
Responsible for customer lifecycle management.
Responsibilities:

- User profile
- Address management
- KYC
- Account status
- User eligibility validation

---

## Merchant Service
Responsible for merchant lifecycle management.
Responsibilities:

- Merchant onboarding
- Merchant approval
- Merchant verification
- Merchant profile management

---

## Order Service
Responsible for order management.
Responsibilities:

- Order creation
- Order validation
- Order lifecycle
- Order status

---

## Payment Service
Responsible for payment execution.
Responsibilities:
- Payment processing
- Payment authorization
- Payment status
- Payment adapters
- Idempotency
- Saga participation

Payment method selection is implemented using the Adapter Pattern for Card, UPI, and Net Banking payment flows.

---

## Settlement Service
Responsible for merchant settlements.
Responsibilities:

- Settlement generation
- Settlement processing
- Settlement completion

---

## Notification Service
Responsible for customer notifications.
Responsibilities:

- RabbitMQ consumers
- Email event processing
- DLQ handling
- Retry mechanisms
- Notification history
(Current implementation publishes notification events and manages retries; real SMTP delivery and template rendering are planned enhancements.)

---

## Audit Service
Responsible for centralized audit logging.
Responsibilities:

- Consume audit events
- Persist business events
- Event querying
- Compliance history

---

## Supporting Infrastructure
Additional platform components include:

- PostgreSQL
- RabbitMQ
- Redis
- Prometheus
- Grafana
- Zipkin
- AlertManager
- Docker
- Kubernetes
- GitHub Actions
- Jenkins

# 14. Service Landscape

PayFlowX is composed of ten independently deployable microservices supported by a centralized API Gateway and a Discovery Server. Each service owns a distinct business capability and its corresponding persistence layer, ensuring loose coupling and clear ownership boundaries.

The platform follows the Database-per-Service pattern, preventing direct database sharing and encouraging communication through well-defined APIs and asynchronous events.

## Service Inventory

| Service | Primary Responsibility | Database | Communication |
|----------|------------------------|-----------|---------------|
| Discovery Server | Service Registry | No | Eureka |
| API Gateway | Request Routing & Authentication | No | REST |
| Auth Service | Authentication & JWT Management | PostgreSQL | REST + RabbitMQ |
| User Service | User Profiles, Addresses & KYC | PostgreSQL | REST + RabbitMQ |
| Merchant Service | Merchant Onboarding & Verification | PostgreSQL | REST + RabbitMQ |
| Order Service | Order Lifecycle Management | PostgreSQL | REST + RabbitMQ |
| Payment Service | Payment Processing | PostgreSQL | REST + RabbitMQ |
| Settlement Service | Merchant Settlements | PostgreSQL | REST + RabbitMQ |
| Notification Service | Notification Processing | PostgreSQL | RabbitMQ |
| Audit Service | Audit Logging | PostgreSQL | RabbitMQ |

---

## Architectural Characteristics

Every service follows a common architectural structure consisting of:

- Controller Layer
- Service Layer
- Repository Layer
- Entity Layer
- DTO Layer
- Exception Handling
- Configuration Layer
- Metrics Integration
- RabbitMQ Integration
- Swagger Documentation

This standardized structure improves maintainability and consistency across the platform.

---

## Service Independence

Each service:

- owns its own business logic
- owns its own database
- exposes its own REST APIs
- publishes its own business events
- can be deployed independently
- can scale independently
- maintains its own observability metrics

This architecture minimizes coupling while maximizing service autonomy.

# 16. Communication Architecture

PayFlowX employs a hybrid communication model that combines synchronous REST communication with asynchronous RabbitMQ messaging.

This approach allows immediate business validation where necessary while leveraging event-driven communication for scalability and loose coupling.

---

## Synchronous Communication

REST APIs are used whenever an immediate response is required.

Typical examples include:

- Authentication validation
- User eligibility validation
- Merchant validation
- Order validation
- Payment initiation
- Settlement queries

Advantages:

- Immediate response
- Request-response semantics
- Simple error handling
- Strong consistency for validation operations

---

## Asynchronous Communication

RabbitMQ is used for business event propagation.

Examples include:

- User Created
- Merchant Approved
- Order Created
- Payment Completed
- Settlement Completed
- Notification Events
- Audit Events

Advantages:

- Loose coupling
- Better scalability
- Improved fault tolerance
- Event replay capability
- Independent service evolution

---

## Communication Patterns

The platform uses the following communication patterns:

### Request-Response

Used for:

- Business validation
- Authentication
- User lookup
- Merchant lookup

---

### Publish-Subscribe

Used for:

- Notifications
- Audit events
- Business events

---

### Event Notification

Services notify interested consumers without requiring knowledge of downstream processing.

---

### Saga Coordination

Long-running business transactions coordinate multiple services through asynchronous event exchange instead of distributed database transactions.

---

## Correlation IDs

Every incoming request carries a Correlation ID.

The Correlation ID is propagated:

- Gateway
- REST requests
- RabbitMQ messages
- Audit events
- Logs
- Zipkin traces

This enables complete end-to-end request tracing across the distributed system.

# 17. Event-Driven Architecture

PayFlowX adopts an event-driven architecture for propagating business state changes between services.

Instead of tightly coupling services through synchronous API chains, business events are published to RabbitMQ exchanges and consumed independently by interested services.

This approach significantly improves scalability, resiliency, and service independence.

---

## Event Sources

Business events originate from multiple services, including:

- Auth Service
- User Service
- Merchant Service
- Order Service
- Payment Service
- Settlement Service

Each service publishes domain events whenever significant business state transitions occur.

---

## Event Consumers

Primary event consumers include:

- Notification Service
- Audit Service
- Saga participants
- Downstream business services

---

## Messaging Infrastructure

RabbitMQ serves as the central messaging backbone.

Messaging infrastructure includes:

- Exchanges
- Queues
- Routing Keys
- Retry Queues
- Dead Letter Queues

---

## Event Reliability
Reliable event delivery is achieved through:
- Transactional Outbox Pattern
- Publisher Confirmations
- Retry Policies
- Dead Letter Queues
- Persistent Messages

---

## Business Events
Examples of published events include:

- User Created
- KYC Submitted
- Merchant Approved
- Order Created
- Payment Initiated
- Payment Completed
- Settlement Created
- Settlement Completed
- Notification Sent

Each event includes sufficient metadata to enable downstream processing without requiring additional database access.

---

## Audit Integration
Every business-critical event is additionally published to the Audit Service.
Audit events contain:

- Event ID
- Correlation ID
- Aggregate ID
- Source Service
- Event Type
- Event Payload
- Timestamp

This provides a centralized and immutable history of business operations across the platform.

---

## Benefits
The event-driven architecture provides:

- Loose coupling
- High scalability
- Independent deployments
- Improved fault tolerance
- Better system extensibility
- Complete business traceability

# 18. Security Architecture
Security is a foundational aspect of the PayFlowX platform. Every request entering the system is authenticated, validated, and authorized before reaching business services. The architecture follows a defense-in-depth approach by combining gateway validation, JWT-based authentication, role-based authorization, secure communication, and comprehensive auditing.

---

## Security Objectives
The platform has been designed to achieve the following security goals:

- Authenticate every incoming request.
- Prevent unauthorized access to protected APIs.
- Maintain stateless authentication.
- Secure inter-service communication.
- Protect sensitive business operations.
- Enable complete auditability of security-sensitive actions.

---

## Security Components
The platform security architecture consists of:

- API Gateway
- Auth Service
- JWT Authentication
- Role-Based Authorization
- Correlation ID propagation
- Audit Service
- HTTPS-ready deployment

---

## Authentication Flow
1. User authenticates using the Auth Service.
2. Credentials are validated.
3. JWT Access Token is generated.
4. Token is returned to the client.
5. Client includes the token in subsequent requests.
6. API Gateway validates the token.
7. User information is forwarded to downstream services using secure request headers.

---

## Protected Resources
Protected operations include:

- User profile management
- Merchant onboarding
- Payment execution
- Settlement processing
- Administrative APIs

---

## Audit Integration
Security-sensitive operations generate audit events that are persisted by the Audit Service for traceability and compliance.

---

## Security Design Principles
- Least Privilege
- Stateless Authentication
- Secure Token Validation
- Defense in Depth
- Centralized Authentication
- Immutable Audit Logs

# 19. Authentication

Authentication is centralized within the Auth Service.

The service is responsible for identity verification and JWT generation while all downstream services rely on forwarded identity information rather than performing authentication independently.

---

## Authentication Features

- User Registration
- User Login
- Password Encryption
- JWT Generation
- Refresh Tokens
- Token Validation

---

## JWT Implementation

PayFlowX supports two JWT signing algorithms:

- HS256
- RS256

Rather than tightly coupling the implementation to a single algorithm, JWT signing is implemented using the Strategy Pattern.

This allows signing algorithms to be selected dynamically without changing authentication business logic.

---

## Strategy Pattern

JWT generation delegates signing responsibilities to strategy implementations.

Benefits include:

- Open for extension
- Easy algorithm replacement
- Cleaner authentication service
- Better maintainability

---

## Token Propagation

After authentication:

- Gateway validates JWT
- Gateway extracts identity
- User ID is forwarded
- Role is forwarded
- Correlation ID is propagated

Downstream services trust the authenticated identity supplied by the Gateway rather than parsing JWTs independently.

# 20. Authorization

Authorization determines whether an authenticated user is permitted to perform a requested operation.

The platform currently supports two system roles:

- USER
- ADMIN

A USER may subsequently enroll as a merchant through the Merchant Service. Merchant status is treated as a business capability rather than a separate authentication role.

---

## USER Responsibilities

A USER can:

- Manage profile
- Manage addresses
- Submit KYC
- Create orders
- Execute payments
- View settlements
- Receive notifications

---

## ADMIN Responsibilities

An ADMIN can:

- Approve merchants
- Reject merchants
- Approve KYC
- Reject KYC
- Manage user accounts
- Access administrative endpoints

---

## Authorization Model

Authorization decisions are based on:

- Authenticated identity
- Assigned role
- Business validation rules

Business rules such as merchant approval or payment eligibility are enforced within the respective business services rather than inside the authentication layer.

# 21. Data Architecture

PayFlowX follows the Database-per-Service architectural pattern.

Each microservice owns its own persistence layer and is solely responsible for managing its data model.

No service performs direct queries against another service's database.

---

## Data Ownership

Each service independently manages:

- Database schema
- Tables
- Transactions
- Entity relationships
- Business constraints

---

## Persistence Technology

Current implementation uses:

- PostgreSQL
- Spring Data JPA
- Hibernate ORM

---

## Entity Management

Business entities are mapped using JPA annotations.

Common characteristics include:

- UUID primary keys
- Audit timestamps
- Domain-specific relationships
- Optimized fetching using EntityGraph where appropriate

---

## Data Isolation

Service isolation provides several benefits:

- Independent deployments
- Independent schema evolution
- Reduced coupling
- Improved scalability
- Better fault isolation

# 22. Database Strategy

Each business service maintains an independent PostgreSQL database.

This strategy prevents tight coupling between services while allowing each domain to evolve independently.

---

## Current Databases

- Auth Database
- User Database
- Merchant Database
- Order Database
- Payment Database
- Settlement Database
- Notification Database
- Audit Database

---

## Database Characteristics

- ACID transactions within a service
- UUID primary keys
- Foreign keys limited to local service boundaries
- Independent schema migration capability

---

## Transaction Boundaries

Database transactions never span multiple services.

Cross-service consistency is achieved through asynchronous messaging and Saga coordination instead of distributed database transactions.

# 23. Messaging Strategy

RabbitMQ serves as the messaging backbone of PayFlowX.

Asynchronous messaging enables services to exchange business events without introducing direct dependencies.

---

## Messaging Objectives

- Loose coupling
- Event propagation
- Reliable delivery
- Retry support
- Dead Letter Queue handling
- Audit integration

---

## Exchange Types

Business events are published through RabbitMQ exchanges using routing keys that identify event categories.

Examples include:

- User events
- Merchant events
- Payment events
- Settlement events
- Notification events
- Audit events

---

## Message Structure

Business messages generally contain:

- Event ID
- Correlation ID
- Aggregate ID
- Source Service
- Event Type
- Business Payload
- Timestamp

---

## Reliability Features

The messaging layer supports:

- Persistent messages
- Retry queues
- Dead Letter Queues
- Publisher confirmations
- Transactional Outbox integration

These mechanisms improve message durability and reduce the likelihood of event loss during failures.

# 24. Distributed Transaction Strategy

PayFlowX operates in a distributed microservices environment where a single business operation often spans multiple independent services and databases. Traditional distributed database transactions (2PC/XA) are intentionally avoided due to their impact on scalability, availability, and operational complexity.

Instead, the platform adopts an event-driven distributed transaction model based on the Saga Pattern.

---

## Design Goals

The distributed transaction strategy aims to:

- Maintain business consistency across services.
- Eliminate distributed database transactions.
- Improve scalability.
- Improve fault tolerance.
- Enable independent service deployment.
- Support eventual consistency.

---

## Transaction Model

Each microservice owns its own database transaction.

Once the local transaction completes successfully, a domain event is published for downstream services.

Business consistency is achieved through event propagation rather than shared transactions.

---

## Local Transactions

Examples include:

- User creation
- Merchant onboarding
- Order creation
- Payment execution
- Settlement generation

Each local transaction commits independently before triggering the next business operation.

---

## Eventual Consistency

Cross-service consistency is achieved asynchronously.

Although services may temporarily observe intermediate states, the overall business workflow converges to a consistent state through event propagation.

This approach significantly improves system scalability while maintaining business correctness.

# 25. Saga Pattern

PayFlowX implements the Saga Pattern to coordinate long-running business workflows involving multiple services.

Rather than relying on distributed transactions, business operations are decomposed into a sequence of independent local transactions coordinated through asynchronous events.

---

## Why Saga?

Traditional two-phase commit (2PC):

- tightly couples services
- reduces availability
- limits scalability

Saga provides:

- loose coupling
- independent services
- better scalability
- improved fault isolation

---

## Current Usage

The Saga Pattern is implemented across business workflows such as:

- Order Processing
- Payment Processing
- Settlement Workflow

Each participant performs its local transaction and publishes the next business event.

---

## Saga Characteristics

The implementation provides:

- Distributed transaction coordination
- Event-driven orchestration
- Service independence
- Eventual consistency
- Failure recovery

---

## Benefits

- No distributed database locks
- Better horizontal scalability
- Higher availability
- Independent service ownership
- Simplified recovery

# 26. Transactional Outbox Pattern

PayFlowX implements the Transactional Outbox Pattern to guarantee reliable event publication.

The pattern prevents inconsistencies that may occur when a database transaction commits successfully but the corresponding RabbitMQ event fails to publish.

---

## Problem

Without an Outbox Pattern:

1. Database transaction commits.
2. RabbitMQ publish fails.
3. Other services never receive the event.

This creates inconsistent business state.

---

## Solution

The Transactional Outbox stores pending events inside the service database as part of the same local transaction.

Only after the transaction commits are events safely published to RabbitMQ.

---

## Benefits

- Atomic event persistence
- Reliable messaging
- No lost business events
- Better failure recovery
- Improved consistency

---

## Platform Usage

Transactional Outbox is used for reliable publication of business events across services participating in distributed workflows.

# 27. Idempotency Strategy

Duplicate requests are common in distributed payment systems due to retries, network failures, and client-side timeouts.

PayFlowX implements an Idempotency Framework to ensure repeated requests produce exactly one business outcome.

---

## Objectives

- Prevent duplicate payments.
- Prevent duplicate order processing.
- Handle client retries safely.
- Support reliable API retries.

---

## Implementation

Incoming requests include an Idempotency Key.

The Payment Service validates whether the request has already been processed.

If a duplicate request is detected:

- Existing response is returned.
- Business logic is skipped.

---

## Advantages

- Exactly-once business processing
- Safe retries
- Duplicate payment prevention
- Improved customer experience

# 28. Caching Strategy

PayFlowX uses Redis to improve performance and reduce unnecessary database access.

Caching is selectively applied where repeated lookups are common and strong consistency is not immediately required.

---

## Objectives

- Reduce database load
- Improve API response time
- Support idempotency
- Improve scalability

---

## Redis Usage

Current platform usage includes:

- Idempotency framework
- Frequently accessed business data
- Short-lived application state

---

## Benefits

- Low latency
- Reduced database traffic
- Better throughput
- Improved horizontal scalability

# 29. Fault Tolerance

PayFlowX is designed to remain operational even when dependent services experience temporary failures.

Fault tolerance mechanisms are implemented at both synchronous and asynchronous communication layers.

---

## Current Mechanisms

The platform implements:

- Resilience4j Circuit Breakers
- Retry Policies
- RabbitMQ Retry Queues
- Dead Letter Queues
- Graceful Failure Handling

---

## Circuit Breakers

Resilience4j Circuit Breakers prevent cascading failures by temporarily blocking requests to unhealthy downstream services.

Benefits include:

- Faster failure detection
- Reduced resource consumption
- Improved service stability

---

## Retry Strategy

Transient failures are automatically retried before considering the operation unsuccessful.

Retries are primarily used for:

- RabbitMQ consumers
- Downstream service communication

---

## Dead Letter Queue

Messages that cannot be processed successfully after configured retries are redirected to Dead Letter Queues.

This prevents infinite retry loops while preserving failed events for later investigation.


# 30. Containerization Strategy

Every PayFlowX microservice is fully containerized using Docker.

Containerization ensures consistent runtime behavior across local development, testing, and production environments.

---

## Docker Components

The platform includes:

- Dockerfiles for every service
- Docker Compose
- Shared network configuration
- Environment-specific configuration

---

## Benefits

- Environment consistency
- Simplified deployment
- Faster onboarding
- Better portability
- Immutable deployments

---

## Supporting Containers

The complete platform includes containers for:

- PostgreSQL
- RabbitMQ
- Redis
- Prometheus
- Grafana
- Zipkin
- AlertManager
- Jenkins
- Business Microservices

# 31. Kubernetes Deployment

PayFlowX is Kubernetes-ready.

Deployment manifests have been prepared for every service to support cloud-native deployment.

---

## Kubernetes Resources

The platform includes:

- Namespace
- Deployment
- Service
- ConfigMap
- Secret
- Horizontal Pod Autoscaler
- Ingress

---

## Deployment Characteristics

The deployment architecture provides:

- Independent service deployment
- Horizontal scaling
- Configuration externalization
- Secret management
- Service discovery
- High availability

---

## Scalability

Each microservice can scale independently based on workload.

Horizontal Pod Autoscalers allow automatic scaling according to resource utilization.

---

## Benefits

- Cloud-native deployment
- Rolling updates
- Zero-downtime deployments
- Fault isolation
- High availability

# 32. CI/CD Architecture

PayFlowX follows an automated Continuous Integration (CI) workflow to ensure every code change is validated before deployment. The platform currently supports GitHub Actions for cloud-based CI and Jenkins for self-hosted pipeline execution.

The CI/CD architecture emphasizes repeatable builds, containerized deployments, automated quality checks, and production-ready artifacts.

---

## Continuous Integration

The GitHub Actions pipeline is triggered on:

- Push to the `main` branch
- Pull Request targeting the `main` branch

For every execution, the pipeline performs the following activities:

1. Checkout repository
2. Configure Java 21
3. Build each microservice independently
4. Execute Maven packaging
5. Build Docker images
6. Authenticate with GitHub Container Registry (GHCR)
7. Push versioned Docker images

Each microservice is built independently using a matrix strategy, enabling parallel execution and reducing overall build time.

---

## Jenkins Pipeline

In addition to GitHub Actions, the project includes a Jenkins pipeline for self-hosted CI environments.

Current Jenkins stages include:

- Repository Checkout
- Parallel Maven Build
- SonarQube Analysis
- Build Status Reporting

The pipeline has been designed to support future deployment stages without requiring architectural changes.

---

## Container Registry

Container images are published to GitHub Container Registry (GHCR).

Benefits include:

- Centralized image management
- Version-controlled artifacts
- Easy Kubernetes integration
- Consistent deployment artifacts

---

## Build Philosophy

Every microservice is treated as an independently deployable unit.

Each service:

- Builds independently
- Produces an independent Docker image
- Can be deployed independently
- Has isolated runtime dependencies

This aligns with the overall microservices architecture of the platform.

# 33. Observability Architecture

Observability is a first-class architectural concern within PayFlowX.

The platform has been designed to provide complete visibility into application health, distributed requests, business metrics, and operational failures.

The observability stack consists of:

- Spring Boot Actuator
- Micrometer
- Prometheus
- Grafana
- Zipkin
- Structured Logback Logging

---

## Objectives

The observability platform enables engineers to answer three fundamental questions:

- What happened?
- Where did it happen?
- Why did it happen?

---

## Metrics Collection

Each microservice exposes operational metrics through Spring Boot Actuator.

Metrics are collected by Prometheus at regular intervals.

Examples include:

- JVM metrics
- HTTP metrics
- Business metrics
- RabbitMQ metrics
- Custom application metrics

---

## Visualization

Grafana provides centralized dashboards for monitoring platform health.

Current dashboards include:

- Platform Health Dashboard
- API Gateway Dashboard
- Merchant Dashboard
- Orders Dashboard
- Payment Dashboard
- Settlement Dashboard
- Notification Dashboard
- Notification DLQ Dashboard

These dashboards provide both infrastructure-level and business-level visibility.

---

## Distributed Tracing

Zipkin enables end-to-end request tracing across multiple services.

Tracing data includes:

- Trace ID
- Span ID
- Service boundaries
- Request timing
- Dependency relationships

This significantly simplifies debugging of distributed workflows.

---

## Health Monitoring

Every service exposes health endpoints through Spring Boot Actuator.

Health checks include:

- Application availability
- Database connectivity
- JVM status
- Metrics availability

These endpoints can be consumed by Kubernetes readiness and liveness probes.

# 34. Logging Strategy

Logging within PayFlowX follows a standardized, structured, and traceable approach.

A shared Logback configuration ensures consistent logging behavior across all microservices.

---

## Objectives

The logging strategy aims to:

- Simplify production debugging
- Enable distributed tracing
- Improve operational visibility
- Maintain consistent log formatting

---

## Structured Logging

Every log entry includes:

- Timestamp
- Log Level
- Thread
- Trace ID
- Span ID
- Correlation ID
- Logger Name
- Service Name
- Log Message

This enables rapid identification of requests across multiple services.

---

## Correlation IDs

Correlation IDs are propagated across:

- API Gateway
- REST communication
- RabbitMQ messages
- Audit events
- Service logs

This enables complete request traceability throughout the platform.

---

## Log Storage

Current implementation provides:

- Console Logging
- Rolling File Logging

Log files are automatically rotated using:

- Daily archival
- Size-based rotation
- Maximum history retention
- Total storage limits

---

## Log Levels

Framework logging has been tuned to reduce unnecessary noise.

Typical configuration includes:

- Spring → INFO
- Hibernate → WARN
- Gateway → INFO
- Business Services → DEBUG

This balance provides detailed application logs without excessive framework verbosity.

# 35. Monitoring & Alerting

Monitoring is implemented using Prometheus, Grafana, and Alertmanager.

The objective is to detect failures before they impact users while providing actionable operational insights.

---

## Prometheus

Prometheus continuously scrapes metrics from every microservice.

Current monitored services include:

- API Gateway
- Auth Service
- User Service
- Merchant Service
- Order Service
- Payment Service
- Settlement Service
- Notification Service

Metrics are collected from each service's `/actuator/prometheus` endpoint.

---

## Grafana

Grafana visualizes platform metrics through production dashboards.

Dashboards provide visibility into:

- Service health
- HTTP traffic
- JVM utilization
- Business metrics
- Payment activity
- Settlement activity
- Notification processing

---

## Alertmanager

Alertmanager processes alerts generated by Prometheus.

Current alerting includes:

- Notification Dead Letter Queue growth

The alerting framework has been structured so additional production alerts can be introduced without architectural changes.

Potential future alerts include:

- High error rates
- Service downtime
- Database latency
- RabbitMQ queue backlog
- JVM memory pressure
- CPU utilization

# 36. Design Patterns Used

PayFlowX intentionally applies established software design patterns to improve extensibility, maintainability, and separation of concerns.

---

## Strategy Pattern

Used within the Auth Service.

Purpose:

Support multiple JWT signing algorithms.

Current implementations:

- HS256
- RS256

Benefits:

- Open/Closed Principle
- Runtime algorithm selection
- Easy extensibility

---

## Adapter Pattern

Used within the Payment Service.

Purpose:

Select payment processor implementation based on the payment method chosen by the customer.

Current adapters include:

- Card Adapter
- UPI Adapter
- Net Banking Adapter

Benefits:

- Loose coupling
- Extensible payment methods
- Cleaner business logic

---

## Repository Pattern

Used across all business services.

Purpose:

Abstract persistence operations from business logic.

Benefits:

- Cleaner service layer
- Better testability
- Reduced persistence coupling

---

## DTO Pattern

Used throughout the platform.

Purpose:

Separate API contracts from persistence entities.

Benefits:

- Stable external APIs
- Improved encapsulation
- Reduced entity exposure

---

## Builder Pattern

Used extensively for:

- Domain Events
- DTO construction
- Entity creation

---

## Dependency Injection

Provided by Spring Framework.

Benefits:

- Loose coupling
- Easier testing
- Better modularity

---

## Layered Architecture

Every microservice follows a consistent layered architecture:

- Controller
- Service
- Repository
- Entity
- DTO
- Configuration

This consistency improves maintainability across the entire platform.

# 37. Technology Stack

The following technologies have been used to build the PayFlowX platform.

| Category | Technology |
|-----------|------------|
| Language | Java 21 |
| Framework | Spring Boot 3.x |
| Build Tool | Maven |
| Database | PostgreSQL |
| ORM | Spring Data JPA + Hibernate |
| Service Discovery | Eureka |
| API Gateway | Spring Cloud Gateway |
| Authentication | JWT (HS256 & RS256) |
| Messaging | RabbitMQ |
| Distributed Transactions | Saga Pattern |
| Reliable Messaging | Transactional Outbox |
| Caching | Redis |
| Fault Tolerance | Resilience4j |
| Mapping | MapStruct |
| API Documentation | OpenAPI / Swagger |
| Metrics | Micrometer |
| Monitoring | Prometheus |
| Dashboards | Grafana |
| Tracing | Zipkin |
| Logging | Logback |
| Containerization | Docker |
| Orchestration | Kubernetes |
| CI | GitHub Actions |
| CI | Jenkins |
| Container Registry | GitHub Container Registry |

# 38. Deployment Architecture

PayFlowX is designed as a cloud-native platform supporting containerized deployments across multiple environments.

---

## Deployment Components

The deployment architecture consists of:

- Kubernetes Cluster
- API Gateway
- Business Services
- RabbitMQ
- PostgreSQL
- Redis
- Prometheus
- Grafana
- Zipkin
- Alertmanager

---

## Deployment Model

Every microservice is packaged as an independent Docker image.

Deployment manifests include:

- Deployment
- Service
- ConfigMap
- Secret
- Horizontal Pod Autoscaler
- Ingress
- Namespace

---

## Deployment Characteristics

- Independent deployment
- Independent scaling
- Rolling updates
- Configuration externalization
- Secret management
- Cloud-native architecture

# 39. Risks & Limitations

The current implementation represents a production-oriented payment platform while acknowledging a small number of planned enhancements.

---

## Current Limitations

- Real SMTP email delivery has not yet been integrated.
- Email template rendering engine has not yet been implemented.

Notification workflows currently process events, retries, persistence, webhooks, and DLQs, but actual email transmission remains a future enhancement.

---

## Operational Considerations

The platform relies on the availability of supporting infrastructure such as:

- RabbitMQ
- PostgreSQL
- Redis
- Eureka
- Prometheus

Appropriate monitoring and redundancy should be configured in production deployments.

---

## Future Evolution

Potential future enhancements include:

- SMTP integration
- Email template engine
- Refund management
- Payment reconciliation engine
- Multi-tenancy support
- OpenTelemetry migration
- Centralized log aggregation (ELK/Loki)

These enhancements can be introduced without requiring fundamental architectural changes due to the modular design of the platform.

# 40. Assumptions & Constraints

This High-Level Design document is based on the current implementation of the PayFlowX platform and the architectural decisions adopted during development.

## Assumptions

The following assumptions have been made throughout the platform design:

- All microservices are independently deployable.
- Each microservice owns its own PostgreSQL database.
- RabbitMQ is available for asynchronous communication.
- Eureka Server is available for service discovery.
- API Gateway is the primary entry point for external clients.
- JWT tokens are generated exclusively by the Auth Service.
- All inter-service communication occurs within a trusted network.
- Correlation IDs are propagated across synchronous and asynchronous communication.
- Services expose health and metrics endpoints through Spring Boot Actuator.
- Docker and Kubernetes are the preferred deployment platforms.

---

## Constraints

The current implementation operates under the following constraints.

### Notification Service

The Notification Service currently supports:

- RabbitMQ Consumers
- Retry Queues
- Dead Letter Queues
- Notification Persistence
- Webhook Processing

Real SMTP email delivery and template rendering are planned future enhancements.

---

### External Payment Providers

The Payment Service currently simulates payment provider integration using internal adapter implementations.

Production integration with external payment gateways (such as Razorpay, Stripe, or PayPal) is outside the scope of the current implementation.

---

### Deployment

Although Kubernetes manifests are included, production deployment infrastructure such as managed Kubernetes clusters, cloud networking, load balancers, and TLS certificates are environment-specific and therefore not part of this project.

---

## Design Constraints

The following architectural decisions intentionally constrain the platform:

- Database sharing between services is prohibited.
- Distributed database transactions are avoided.
- Eventual consistency is preferred over two-phase commit.
- Business communication occurs through REST or RabbitMQ only.
- Every service remains independently deployable.

# 41. Glossary

| Term | Description |
|------|-------------|
| API Gateway | Single entry point for all external client requests |
| Eureka | Service Discovery server used for dynamic service registration |
| JWT | JSON Web Token used for stateless authentication |
| HS256 | HMAC SHA-256 JWT signing algorithm |
| RS256 | RSA SHA-256 JWT signing algorithm |
| RabbitMQ | Message broker used for asynchronous communication |
| DLQ | Dead Letter Queue used for failed message handling |
| Saga | Distributed transaction coordination pattern |
| Transactional Outbox | Reliable event publishing pattern |
| Idempotency | Ability to safely process duplicate requests only once |
| Correlation ID | Unique identifier used for tracing requests across services |
| Trace ID | Distributed tracing identifier generated by Zipkin |
| Span | Individual execution segment within a distributed trace |
| Redis | In-memory data store used for caching and idempotency |
| Prometheus | Metrics collection platform |
| Grafana | Metrics visualization platform |
| Zipkin | Distributed tracing platform |
| HPA | Horizontal Pod Autoscaler in Kubernetes |
| ConfigMap | Kubernetes resource for configuration management |
| Secret | Kubernetes resource for sensitive configuration |
| Actuator | Spring Boot operational endpoints |
| DTO | Data Transfer Object |
| Entity | Persistent domain model |
| Repository | Persistence abstraction layer |
| Controller | REST API entry layer |
| Service | Business logic layer |


# 42. References

The architecture presented in this document is based on the following technologies, standards, and official documentation.

## Frameworks

- Spring Boot
- Spring Cloud
- Spring Security
- Spring Data JPA
- Spring Cloud Gateway
- Spring Cloud Netflix Eureka

---

## Messaging

- RabbitMQ

---

## Database

- PostgreSQL

---

## Monitoring

- Prometheus
- Grafana
- Micrometer
- Spring Boot Actuator
- Zipkin

---

## Containerization

- Docker
- Docker Compose
- Kubernetes

---

## Build & CI

- Maven
- GitHub Actions
- Jenkins

---

## API Documentation

- OpenAPI Specification
- Swagger UI

---

## Design Patterns

The platform applies several established software engineering patterns, including:

- Strategy Pattern
- Adapter Pattern
- Repository Pattern
- Builder Pattern
- Dependency Injection
- Layered Architecture
- Saga Pattern
- Transactional Outbox Pattern

# 43. Appendix

This section provides supplementary information related to the PayFlowX platform.

---

## Platform Summary

Project Name:

PayFlowX

Project Type:

Enterprise Payment Processing Platform

Architecture:

Distributed Microservices

Language:

Java 21

Framework:

Spring Boot

---

## Microservices

The platform currently consists of ten independently deployable microservices.

| Service |
|----------|
| Discovery Server |
| API Gateway |
| Auth Service |
| User Service |
| Merchant Service |
| Order Service |
| Payment Service |
| Settlement Service |
| Notification Service |
| Audit Service |

---

## Infrastructure Components

Supporting infrastructure includes:

- PostgreSQL
- RabbitMQ
- Redis
- Prometheus
- Grafana
- Zipkin
- Alertmanager
- Docker
- Kubernetes
- GitHub Container Registry

---

## Monitoring Assets

Current Grafana dashboards include:

- Platform Health Dashboard
- API Gateway Dashboard
- Merchant Dashboard
- Orders Dashboard
- Payment Dashboard
- Settlement Dashboard
- Notification Dashboard
- Notification DLQ Dashboard

---

## Kubernetes Resources

Deployment manifests currently include:

- Namespace
- Deployment
- Service
- ConfigMap
- Secret
- Horizontal Pod Autoscaler
- Ingress

for every deployable microservice.

---

## CI/CD Assets

The repository includes:

- GitHub Actions CI Pipeline
- Jenkins Pipeline
- Dockerfiles for all services
- Docker Compose configuration
- Kubernetes deployment manifests

---

## Documentation Set

The complete PayFlowX documentation consists of:

- README.md
- High-Level Design (HLD)
- Low-Level Design (LLD)
- Software Architecture Document
- Service Documentation
- Architecture Diagrams
- API Documentation
- ADRs
- CHANGELOG

---

## Version

Document Version: 1.0

Status: Approved

Prepared For:

PayFlowX Platform

