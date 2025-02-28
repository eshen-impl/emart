package com.chuwa.orderservice.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PaymentEvent {
    private String eventType;
    private UUID userId;

    private UUID orderId;
    private String paymentCardLast4;
    private Instant paymentTimestamp;
}

