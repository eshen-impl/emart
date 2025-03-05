package com.chuwa.shippingservice.consumer;


import com.chuwa.shippingservice.payload.PaymentEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class PaymentEventConsumer {
    private static final Logger LOGGER = LoggerFactory.getLogger(PaymentEventConsumer.class);
    @KafkaListener(topics = "payment-to-shipping", groupId = "shipping-group",
            containerFactory = "paymentEventListenerFactory")
    public void listenPaymentUpdates(PaymentEvent event) {

        try {
            LOGGER.info("Shipping Service received payment event: {}", event);
            LOGGER.info("Payment Event - Type: {}, User ID: {}, Order ID: {}, ",
                    event.getEventType(), event.getUserId(), event.getOrderId());

        } catch (Exception e) {
            LOGGER.error("Error deserializing or processing payment event: {}", e.getMessage(), e);
        }
    }
}
