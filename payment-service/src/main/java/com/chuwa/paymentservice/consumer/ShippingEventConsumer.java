package com.chuwa.paymentservice.consumer;

import com.chuwa.paymentservice.payload.ShippingEvent;
import com.chuwa.paymentservice.service.PaymentService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;


@Component
public class ShippingEventConsumer {
    private static final Logger LOGGER = LoggerFactory.getLogger(ShippingEventConsumer.class);

    private final PaymentService paymentService;

    public ShippingEventConsumer(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @KafkaListener(topics = "shipping-to-payment", groupId = "payment-group",
            containerFactory = "shippingEventListenerFactory")
    public void listenShippingUpdates(ShippingEvent event) {

        try {
            LOGGER.info("Payment Service received shipping event: {}", event);
            paymentService.processShippingResponse(event);
            LOGGER.info("Payment processed successfully for Order ID: {}, type: {}", event.getOrderId(), event.getEventType());
        } catch (Exception e) {
            LOGGER.error("Error deserializing or processing payment response: {}", e.getMessage(), e);
        }
    }
}
