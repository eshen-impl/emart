package com.chuwa.shippingservice.producer;


import com.chuwa.shippingservice.payload.ShippingEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class ShippingEventProducer {
    private final KafkaTemplate<String, ShippingEvent> kafkaTemplate;

    public ShippingEventProducer(KafkaTemplate<String, ShippingEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendToPayment(ShippingEvent event) {
        kafkaTemplate.send("shipping-to-payment", event);
    }

    public void sendToOrder(ShippingEvent event) {
        kafkaTemplate.send("shipping-to-order", event);
    }

}
