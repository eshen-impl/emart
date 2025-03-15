### Microservices Architecture
![emart arch drawio](https://github.com/user-attachments/assets/d1badc74-2e2e-4256-afd1-865255e06aed)

### Data Flow

![Emart data flow drawio](https://github.com/user-attachments/assets/8a9e1985-d0fa-45a3-bc7e-ec35ae8b6513)

### User Flow

![user flow drawio](https://github.com/user-attachments/assets/d0935031-30b7-472c-9449-48828fa66b6e)



### Order & Payment State Transition Flow

| Event                                          | Order Status                                | Payment Status                                               | Interservice Call (Sync/Async)                               |
| ---------------------------------------------- | ------------------------------------------- | ------------------------------------------------------------ | :----------------------------------------------------------- |
| User places an order                           | CREATED                                     | PENDING (if payment service unavailable / any exception thrown) | Sync (1. Order → Item: check availability; 2. Account: get address / paymentmethod; 3. Payment: validate card) |
| Payment validation success                     | CREATED                                     | VALIDATED                                                    | Async (Order → Kafka  → Shipping)                            |
| Payment validation fails                       | CANCELED                                    | FAILED                                                       |                                                              |
| User adds/removes item(s) to an existing order | UPDATED                                     | ADJUSTMENT_PENDING (if payment service unavailable / any exception thrown) | Sync (Order → Item: check availability; Order → Payment: validate update) |
| Payment validation success for update          | UPDATED                                     | VALIDATED (new) VOIDED (original)                            | Async (Order → Kafka → Shipping)                             |
| Payment validation fails for update            | SUSPENDED                                   | FAILED (new)      VOIDED (original)                          | Async (Order → Kafka → Shipping)                             |
| Shipping Service processes order               | with status: CREATED/UPDATED                | with status:  VALIDATED                                      | Async (Shipping → Kafka → Payment)                           |
| Payment processed successfully                 | SHIPPED                                     | PAID                                                         | Async (Payment → Kafka → Order/Shipping)                     |
| Payment failed                                 | SUSPENDED                                   | FAILED                                                       | Async (Payment → Kafka → Order/Shipping)                     |
| User cancels order before shipping             | CANCELED                                    | CANCELED                                                     | Sync (Order → Payment: cancel authorization) + Async (Order → Kafka → Shipping) |
| Order marked as delivered                      | DELIVERED                                   | PAID                                                         | Async (Shipping → Kafka → Order)                             |
| Refund requested after delivery                | DELIVERED (order) REFUND_REQUESTED (refund) | PAID (payment) REFUND_REQUESTED (refund)                     | Sync (Order → Payment: initiate refund)                      |
| Refund approved and processed                  | DELIVERED (order) REFUNDED (refund)         | PAID (payment) REFUNDED (refund)                             | Async (Payment → Kafka → Order)                              |



### Deployment

Run docker-compose up -d  in your root directory

http://localhost:8080/swagger-ui/index.html - account-service

http://localhost:8081/swagger-ui/index.html - item-service

http://localhost:8082/swagger-ui/index.html - cart-service

http://localhost:8083/swagger-ui/index.html - order-service

http://localhost:8084/swagger-ui/index.html - payment-service

http://localhost:8085/swagger-ui/index.html - shipping-service

http://localhost:8761/ - Eureka server

http://localhost:8088/ - Kafka UI
