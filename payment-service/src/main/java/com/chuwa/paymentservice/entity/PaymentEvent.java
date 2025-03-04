package com.chuwa.paymentservice.entity;

import com.chuwa.paymentservice.enums.PaymentEventType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PaymentEvent {
    private PaymentEventType eventType;
    private UUID userId;
    private UUID orderId;

    private BigDecimal refundedAmount;

//    private String paymentCardLast4;
//    private Instant paymentTimestamp;
}

