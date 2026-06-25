# PayFlowX - Services Breakdown

## 1. discovery-server
Registers and discovers all microservices.

## 2. api-gateway
Single entry point for clients.
Routing, auth filters, rate limiting.

## 3. auth-service
Login, token generation, user roles.

## 4. merchant-service
Merchant onboarding, API credentials, webhook setup.

## 5. order-service
Order creation and retrieval.

## 6. payment-service
Payment initiation, state transitions, idempotency.

## 7. refund-service
Refund requests and processing.

## 8. ledger-service
Accounting records for all transactions.

## 9. notification-service
Send internal or external notifications.

## 10. webhook-service
Dispatch signed webhook events with retries.