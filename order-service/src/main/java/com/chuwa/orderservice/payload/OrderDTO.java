package com.chuwa.orderservice.payload;

import com.chuwa.orderservice.enums.OrderStatus;
import com.chuwa.orderservice.enums.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.cassandra.core.mapping.Column;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderDTO {

    private UUID orderId;
    private UUID userId;
    private OrderStatus orderStatus;
    private BigDecimal totalAmount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String items;
    private PaymentStatus paymentStatus;
    private String shippingAddress;
    private String billingAddress;
    private String paymentMethod;


}
