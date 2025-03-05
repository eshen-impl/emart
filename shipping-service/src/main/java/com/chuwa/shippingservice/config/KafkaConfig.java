package com.chuwa.shippingservice.config;


import com.chuwa.shippingservice.payload.OrderEvent;
import com.chuwa.shippingservice.payload.PaymentEvent;
import com.chuwa.shippingservice.payload.ShippingEvent;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.*;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConfig {
    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Bean
    public ProducerFactory<String, ShippingEvent> producerFactory() {
        Map<String, Object> config = new HashMap<>();
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return new DefaultKafkaProducerFactory<>(config);
    }

    @Bean
    public KafkaTemplate<String, ShippingEvent> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }

    private Map<String, Object> consumerConfigs() {
        return Map.of(
                "bootstrap.servers", bootstrapServers,
                "group.id", "shipping-group",
                "auto.offset.reset", "earliest"
        );
    }
    @Bean
    @Qualifier("orderEventConsumerFactory")
    public ConsumerFactory<String, OrderEvent> orderEventConsumerFactory() {
        JsonDeserializer<OrderEvent> deserializer = new JsonDeserializer<>(OrderEvent.class, false);
        deserializer.addTrustedPackages("*");

        return new DefaultKafkaConsumerFactory<>(
                consumerConfigs(),
                new StringDeserializer(),
                deserializer
        );
    }

    @Bean
    @Qualifier("paymentEventConsumerFactory")
    public ConsumerFactory<String, PaymentEvent> paymentEventConsumerFactory() {
        JsonDeserializer<PaymentEvent> deserializer = new JsonDeserializer<>(PaymentEvent.class, false);
        deserializer.addTrustedPackages("*");

        return new DefaultKafkaConsumerFactory<>(
                consumerConfigs(),
                new StringDeserializer(),
                deserializer
        );
    }


    @Bean
    @Qualifier("orderEventListenerFactory")
    public ConcurrentKafkaListenerContainerFactory<String, OrderEvent> orderEventListenerFactory(
            @Qualifier("orderEventConsumerFactory") ConsumerFactory<String, OrderEvent> consumerFactory) {
        ConcurrentKafkaListenerContainerFactory<String, OrderEvent> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);
        return factory;
    }

    @Bean
    @Qualifier("paymentEventListenerFactory")
    public ConcurrentKafkaListenerContainerFactory<String, PaymentEvent> paymentEventListenerFactory(
            @Qualifier("paymentEventConsumerFactory") ConsumerFactory<String, PaymentEvent> consumerFactory) {
        ConcurrentKafkaListenerContainerFactory<String, PaymentEvent> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);
        return factory;
    }


    @Bean
    public NewTopic shippingToPaymentTopic() {
        return new NewTopic("shipping-to-payment", 3, (short) 1);
    }

    @Bean
    public NewTopic shippingToOrderTopic() {
        return new NewTopic("shipping-to-order", 3, (short) 1);
    }
}


