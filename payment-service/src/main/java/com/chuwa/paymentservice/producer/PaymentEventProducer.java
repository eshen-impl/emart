package com.chuwa.paymentservice.producer;



import com.chuwa.paymentservice.payload.PaymentEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class PaymentEventProducer {
    private final KafkaTemplate<String, PaymentEvent> kafkaTemplate;

    public PaymentEventProducer(KafkaTemplate<String, PaymentEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendToShipping(PaymentEvent event) {
        kafkaTemplate.send("payment-to-shipping", event);
    }

    public void sendToOrder(PaymentEvent event) {
        kafkaTemplate.send("payment-to-order", event);
    }

}
