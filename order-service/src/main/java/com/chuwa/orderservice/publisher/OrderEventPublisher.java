package com.chuwa.orderservice.publisher;


import com.chuwa.orderservice.entity.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class OrderEventPublisher {
    @Autowired
    private KafkaTemplate<String, Order> kafkaTemplate;

    @Value("${order.event.topicName}")
    private String topicName;

    public void sendOrderEvent(Order order) {
        kafkaTemplate.send("order_events", order);
    }
}
