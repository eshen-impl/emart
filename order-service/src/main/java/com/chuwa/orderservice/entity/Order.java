package com.chuwa.orderservice.entity;

import com.chuwa.orderservice.enums.OrderStatus;
import com.chuwa.orderservice.enums.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.core.mapping.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table("orders")
public class Order {

    @PrimaryKeyColumn(name = "order_id", type = PrimaryKeyType.PARTITIONED)
    private UUID orderId;
    @Column("user_id")
    private UUID userId;

    @Column("order_status")
    private OrderStatus orderStatus;

    @Column("total_amount")
    private BigDecimal totalAmount;

    @Column("created_at")
    private LocalDateTime createdAt;

    @Column("updated_at")
    private LocalDateTime updatedAt;

    @Column("items")
    private String items; //List of items in JSON

    @Column("payment_status")
    private PaymentStatus paymentStatus;

    @Column("shipping_address")
    private String shippingAddress;  // Full address snapshot in JSON

    @Column("billing_address")
    private String billingAddress;  // Full address snapshot in JSON

    @Column("payment_method")
    private String paymentMethod; // only necessary metadata snapshot from the payment method


}
