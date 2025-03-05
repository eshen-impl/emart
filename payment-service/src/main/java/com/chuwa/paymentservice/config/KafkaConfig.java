package com.chuwa.paymentservice.config;


import com.chuwa.paymentservice.payload.PaymentEvent;
import com.chuwa.paymentservice.payload.ShippingEvent;
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
    public ProducerFactory<String, PaymentEvent> producerFactory() {
        Map<String, Object> config = new HashMap<>();
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return new DefaultKafkaProducerFactory<>(config);
    }

    @Bean
    public KafkaTemplate<String, PaymentEvent> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }

    private Map<String, Object> consumerConfigs() {
        return Map.of(
                "bootstrap.servers", bootstrapServers,
                "group.id", "payment-group",
                "auto.offset.reset", "earliest"
        );
    }

    @Bean
    @Qualifier("shippingEventConsumerFactory")
    public ConsumerFactory<String, ShippingEvent> shippingEventConsumerFactory() {
        JsonDeserializer<ShippingEvent> deserializer = new JsonDeserializer<>(ShippingEvent.class, false);
        deserializer.addTrustedPackages("*");

        return new DefaultKafkaConsumerFactory<>(
                consumerConfigs(),
                new StringDeserializer(),
                deserializer
        );
    }

    @Bean
    @Qualifier("shippingEventListenerFactory")
    public ConcurrentKafkaListenerContainerFactory<String, ShippingEvent> shippingEventListenerFactory(
            @Qualifier("shippingEventConsumerFactory") ConsumerFactory<String, ShippingEvent> consumerFactory) {
        ConcurrentKafkaListenerContainerFactory<String, ShippingEvent> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);
        return factory;
    }

    @Bean
    public NewTopic paymentToOrderTopic() {
        return new NewTopic("payment-to-order", 3, (short) 1);
    }

    @Bean
    public NewTopic paymentToShippingTopic() {
        return new NewTopic("payment-to-shipping", 3, (short) 1);
    }


}


