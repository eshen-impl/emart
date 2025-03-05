package com.chuwa.shippingservice.consumer;

import com.chuwa.shippingservice.payload.OrderEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class OrderEventConsumer {
    private static final Logger LOGGER = LoggerFactory.getLogger(OrderEventConsumer.class);
    @KafkaListener(topics = "order-to-shipping", groupId = "shipping-group",
            containerFactory = "orderEventListenerFactory")
    public void listenOrderUpdates(OrderEvent event) {

        try {
            LOGGER.info("Shipping Service received order event: {}", event);
            LOGGER.info("Order Event - Type: {}, Order ID: {}, User ID: {}, Items: {}, Shipping Address: {}, Created At: {}, Updated At: {}",
                    event.getEventType(), event.getOrderId(), event.getUserId(), event.getItems(),
                    event.getShippingAddress(), event.getCreatedAt(), event.getUpdatedAt());

        } catch (Exception e) {
            LOGGER.error("Error deserializing or processing order event: {}", e.getMessage(), e);
        }
    }
}
